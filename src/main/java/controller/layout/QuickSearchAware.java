package controller.layout;

public interface QuickSearchAware {

    default String getQuickSearchPrompt() {
        return "Tìm kiếm nhanh...";
    }

    void applyQuickSearch(String keyword);
}
