package util;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.EventTarget;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeView;
import javafx.scene.input.ScrollEvent;
import javafx.util.Duration;

public final class SmoothScrollUtil {

    private static final String INSTALLED_KEY = SmoothScrollUtil.class.getName() + ".installed";
    private static final String BAR_KEY = SmoothScrollUtil.class.getName() + ".verticalBar";
    private static final String ANIMATION_KEY = SmoothScrollUtil.class.getName() + ".animation";
    private static final String TARGET_VALUE_KEY = SmoothScrollUtil.class.getName() + ".targetValue";

    private static final double SCROLL_PANE_MULTIPLIER = 1.22;
    private static final double TABLE_PIXEL_MULTIPLIER = 1.10;
    private static final double TABLE_ROW_MULTIPLIER = 2.70;
    private static final double DEFAULT_ROW_HEIGHT = 44.0;
    private static final double ANIMATION_MILLIS = 180.0;

    private SmoothScrollUtil() {
    }

    public static void install(Node root) {
        if (root == null) {
            return;
        }

        installNode(root);
        if (root instanceof ScrollPane scrollPane) {
            install(scrollPane.getContent());
        }
        if (root instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                install(child);
            }
        }
    }

    private static void installNode(Node node) {
        if (node instanceof ScrollPane scrollPane) {
            installScrollPane(scrollPane);
        }
        if (isVirtualizedScrollControl(node)) {
            installVirtualizedControl((Control) node);
        }
    }

    private static void installScrollPane(ScrollPane scrollPane) {
        if (isInstalled(scrollPane)) {
            return;
        }
        markInstalled(scrollPane);
        addStyleClass(scrollPane, "smooth-scroll-target");

        scrollPane.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (!isVerticalWheelEvent(event) || isFromNestedScrollable(event.getTarget(), scrollPane)) {
                return;
            }

            double viewportHeight = scrollPane.getViewportBounds().getHeight();
            Node content = scrollPane.getContent();
            if (content == null || viewportHeight <= 0) {
                return;
            }

            double contentHeight = content.getLayoutBounds().getHeight();
            double scrollableHeight = Math.max(0, contentHeight - viewportHeight);
            if (scrollableHeight <= 1) {
                return;
            }

            double currentPixel = resolveAnimationBaseValue(scrollPane, scrollPane.getVvalue()) * scrollableHeight;
            double targetPixel = clamp(
                    currentPixel - (event.getDeltaY() * SCROLL_PANE_MULTIPLIER),
                    0,
                    scrollableHeight);
            double targetValue = targetPixel / scrollableHeight;

            if (Math.abs(targetValue - scrollPane.getVvalue()) < 0.0001) {
                return;
            }

            animateScrollPane(scrollPane, targetValue);
            event.consume();
        });
    }

    private static void installVirtualizedControl(Control control) {
        if (isInstalled(control)) {
            return;
        }
        markInstalled(control);
        addStyleClass(control, "smooth-scroll-target");

        control.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (!isVerticalWheelEvent(event)) {
                return;
            }

            ScrollBar verticalBar = findVerticalScrollBar(control);
            if (verticalBar == null || !verticalBar.isVisible() || verticalBar.isDisabled()) {
                return;
            }

            double min = verticalBar.getMin();
            double max = verticalBar.getMax();
            if (max <= min) {
                return;
            }

            double targetValue = computeScrollBarTarget(control, verticalBar, event);
            if (Math.abs(targetValue - verticalBar.getValue()) < 0.0001) {
                return;
            }

            animateScrollBar(verticalBar, targetValue);
            event.consume();
        });

        Platform.runLater(() -> findVerticalScrollBar(control));
    }

    private static boolean isVirtualizedScrollControl(Node node) {
        return node instanceof TableView<?> || node instanceof ListView<?> || node instanceof TreeView<?>;
    }

    private static boolean isVerticalWheelEvent(ScrollEvent event) {
        return Math.abs(event.getDeltaY()) > 0.01 && !event.isControlDown();
    }

    private static boolean isFromNestedScrollable(EventTarget target, ScrollPane owner) {
        if (!(target instanceof Node node)) {
            return false;
        }

        Node cursor = node;
        while (cursor != null) {
            if (cursor == owner) {
                return false;
            }
            if (cursor instanceof ScrollPane || isVirtualizedScrollControl(cursor)) {
                return true;
            }
            cursor = cursor.getParent();
        }
        return false;
    }

    private static ScrollBar findVerticalScrollBar(Control control) {
        Object cached = control.getProperties().get(BAR_KEY);
        if (cached instanceof ScrollBar scrollBar && scrollBar.getScene() != null) {
            return scrollBar;
        }

        for (Node node : control.lookupAll(".scroll-bar")) {
            if (node instanceof ScrollBar scrollBar && scrollBar.getOrientation() == Orientation.VERTICAL) {
                control.getProperties().put(BAR_KEY, scrollBar);
                return scrollBar;
            }
        }
        return null;
    }

    private static double computeScrollBarTarget(Control control, ScrollBar scrollBar, ScrollEvent event) {
        double min = scrollBar.getMin();
        double max = scrollBar.getMax();
        double range = max - min;
        double wheelDelta = -event.getDeltaY();
        double deltaValue;

        if (range <= 1.0) {
            double rowHeight = resolveRowHeight(control);
            double visibleRows = Math.max(1.0, control.getHeight() / rowHeight);
            double itemCount = resolveItemCount(control);
            double scrollableRows = Math.max(visibleRows, itemCount - visibleRows);
            deltaValue = (wheelDelta / rowHeight) * TABLE_ROW_MULTIPLIER / scrollableRows * range;
        } else {
            deltaValue = wheelDelta * TABLE_PIXEL_MULTIPLIER;
        }

        double baseValue = resolveAnimationBaseValue(scrollBar, scrollBar.getValue());
        return clamp(baseValue + deltaValue, min, max);
    }

    private static double resolveRowHeight(Control control) {
        if (control instanceof TableView<?> tableView && tableView.getFixedCellSize() > 0) {
            return tableView.getFixedCellSize();
        }
        if (control instanceof ListView<?> listView && listView.getFixedCellSize() > 0) {
            return listView.getFixedCellSize();
        }
        return DEFAULT_ROW_HEIGHT;
    }

    private static double resolveItemCount(Control control) {
        if (control instanceof TableView<?> tableView && tableView.getItems() != null) {
            return tableView.getItems().size();
        }
        if (control instanceof ListView<?> listView && listView.getItems() != null) {
            return listView.getItems().size();
        }
        if (control instanceof TreeView<?> treeView) {
            return treeView.getExpandedItemCount();
        }
        return 0;
    }

    private static void animateScrollPane(ScrollPane scrollPane, double targetValue) {
        stopRunningAnimation(scrollPane);
        scrollPane.getProperties().put(TARGET_VALUE_KEY, targetValue);
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(ANIMATION_MILLIS),
                new KeyValue(scrollPane.vvalueProperty(), targetValue, Interpolator.EASE_OUT)));
        rememberAnimation(scrollPane, timeline);
        timeline.play();
    }

    private static void animateScrollBar(ScrollBar scrollBar, double targetValue) {
        stopRunningAnimation(scrollBar);
        scrollBar.getProperties().put(TARGET_VALUE_KEY, targetValue);
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(ANIMATION_MILLIS),
                new KeyValue(scrollBar.valueProperty(), targetValue, Interpolator.EASE_OUT)));
        rememberAnimation(scrollBar, timeline);
        timeline.play();
    }

    private static void stopRunningAnimation(Node node) {
        Object running = node.getProperties().get(ANIMATION_KEY);
        if (running instanceof Timeline timeline) {
            timeline.stop();
        }
    }

    private static void rememberAnimation(Node node, Timeline timeline) {
        node.getProperties().put(ANIMATION_KEY, timeline);
        timeline.setOnFinished(event -> {
            if (node.getProperties().get(ANIMATION_KEY) == timeline) {
                node.getProperties().remove(ANIMATION_KEY);
                node.getProperties().remove(TARGET_VALUE_KEY);
            }
        });
    }

    private static double resolveAnimationBaseValue(Node node, double fallback) {
        Object target = node.getProperties().get(TARGET_VALUE_KEY);
        if (target instanceof Number number) {
            return number.doubleValue();
        }
        return fallback;
    }

    private static boolean isInstalled(Node node) {
        return Boolean.TRUE.equals(node.getProperties().get(INSTALLED_KEY));
    }

    private static void markInstalled(Node node) {
        node.getProperties().put(INSTALLED_KEY, Boolean.TRUE);
    }

    private static void addStyleClass(Node node, String styleClass) {
        if (!node.getStyleClass().contains(styleClass)) {
            node.getStyleClass().add(styleClass);
        }
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
