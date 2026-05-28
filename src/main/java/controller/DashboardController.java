package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import model.dao.DocumentAlertDAO;
import model.dao.RoleDAO;
import model.dao.UserDAO;
import model.dao.VehicleDAO;
import model.entity.Role;
import model.entity.User;
import model.entity.Vehicle;
import session.UserSession;

import java.util.List;
import java.util.Locale;

public class DashboardController {

    @FXML private Label lblTitle;
    @FXML private Label lblSubtitle;
    @FXML private Label lblRoleBadge;

    @FXML private Label lblKpiOneTitle;
    @FXML private Label lblKpiOneValue;
    @FXML private Label lblKpiOneHint;
    @FXML private Label lblKpiTwoTitle;
    @FXML private Label lblKpiTwoValue;
    @FXML private Label lblKpiTwoHint;
    @FXML private Label lblKpiThreeTitle;
    @FXML private Label lblKpiThreeValue;
    @FXML private Label lblKpiThreeHint;
    @FXML private Label lblKpiFourTitle;
    @FXML private Label lblKpiFourValue;
    @FXML private Label lblKpiFourHint;

    @FXML private Label lblModuleOneTitle;
    @FXML private Label lblModuleOneText;
    @FXML private Label lblModuleOneBadge;
    @FXML private Label lblModuleTwoTitle;
    @FXML private Label lblModuleTwoText;
    @FXML private Label lblModuleTwoBadge;
    @FXML private Label lblModuleThreeTitle;
    @FXML private Label lblModuleThreeText;
    @FXML private Label lblModuleThreeBadge;
    @FXML private VBox moduleFour;
    @FXML private Label lblModuleFourTitle;
    @FXML private Label lblModuleFourText;
    @FXML private Label lblModuleFourBadge;

    @FXML private Label lblFocusTitle;
    @FXML private Label lblFocusText;
    @FXML private Label lblCheckOne;
    @FXML private Label lblCheckTwo;
    @FXML private Label lblCheckThree;

    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final DocumentAlertDAO documentAlertDAO = new DocumentAlertDAO();
    private final UserDAO userDAO = new UserDAO();
    private final RoleDAO roleDAO = new RoleDAO();

    @FXML
    public void initialize() {
        Role role = UserSession.getInstance().getCurrentRole();
        User user = UserSession.getInstance().getCurrentUser();
        String roleCode = normalizeRoleCode(role == null ? null : role.getRoleCode());
        String displayName = resolveDisplayName(user);

        switch (roleCode) {
            case "ADMIN" -> renderAdmin(displayName);
            case "MANAGER" -> renderManager(displayName);
            case "TECH" -> renderTech(displayName);
            default -> renderDefault(displayName);
        }
    }

    private void renderAdmin(String displayName) {
        List<User> users = userDAO.findAll();
        long lockedUsers = users.stream()
                .filter(user -> "LOCKED".equalsIgnoreCase(nullToEmpty(user.getAccountStatus())))
                .count();

        setHeader("Xin chào, " + displayName,
                "Quản trị tài khoản, vai trò và trạng thái vận hành hệ thống.",
                "ADMIN");
        setKpis("Tài khoản", users.size(), "Người dùng đã tạo",
                "Vai trò", roleDAO.findAllActive().size(), "Nhóm quyền đang kích hoạt",
                "Bị khóa", lockedUsers, "Tài khoản cần xem lại",
                "Phương tiện", vehicleDAO.findAll().size(), "Dữ liệu nghiệp vụ hiện có");
        setModules("Quản lý người dùng", "Tạo tài khoản, sửa thông tin, khóa và mở khóa.", "ADMIN",
                "Vai trò & phân quyền", "Kiểm tra nhóm quyền cho Admin, Manager và Tech.", "RBAC",
                "Tổng quan hệ thống", "Theo dõi số liệu tổng hợp khi demo và nghiệm thu.", "OVERVIEW",
                "Báo cáo", "Truy cập dữ liệu tổng hợp để đối chiếu nhanh.", "READ ONLY");
        setFocus("Cần kiểm tra trước khi bàn giao",
                "Đảm bảo mỗi thành viên có tài khoản đúng vai trò và tài khoản demo đang hoạt động.",
                "1. Tạo đủ tài khoản admin, manager, tech.",
                "2. Thử đăng nhập và đăng xuất trên từng role.",
                "3. Kiểm tra menu ẩn/hiện đúng phân quyền.");
    }

    private void renderManager(String displayName) {
        List<Vehicle> vehicles = vehicleDAO.findAll();
        long underMaintenance = vehicles.stream()
                .filter(vehicle -> "UNDER_MAINTENANCE".equalsIgnoreCase(nullToEmpty(vehicle.getVehicleStatus())))
                .count();

        setHeader("Xin chào, " + displayName,
                "Theo dõi phương tiện, giấy tờ pháp lý, cảnh báo và báo cáo đội xe.",
                "MANAGER");
        setKpis("Tổng xe", vehicles.size(), "Hồ sơ phương tiện đang quản lý",
                "Bảo dưỡng", underMaintenance, "Xe đang ở trạng thái bảo dưỡng",
                "Sắp hết hạn", documentAlertDAO.countByStatus("COMING_DUE"), "Giấy tờ cần theo dõi",
                "Đã hết hạn", documentAlertDAO.countByStatus("OVERDUE"), "Cần xử lý ưu tiên");
        setModules("Hồ sơ phương tiện", "Quản lý biển số, loại xe, ODO, số khung và số máy.", "CORE",
                "Giấy tờ xe", "Cập nhật đăng kiểm, bảo hiểm và phí đường bộ.", "DOCUMENT",
                "Cảnh báo giấy tờ", "Lọc giấy tờ sắp hết hạn hoặc đã quá hạn.", "ALERT",
                "Báo cáo chi phí", "Theo dõi chi phí khi module báo cáo được kết nối.", "REPORT");
        setFocus("Ưu tiên vận hành đội xe",
                "Xử lý giấy tờ đã hết hạn trước, sau đó bổ sung hồ sơ xe và cập nhật cảnh báo còn lại.",
                "1. Mở Cảnh báo giấy tờ để xem xe quá hạn.",
                "2. Cập nhật giấy tờ mới cho từng xe.",
                "3. Thêm hoặc sửa hồ sơ phương tiện phát sinh.");
    }

    private void renderTech(String displayName) {
        long underMaintenance = vehicleDAO.findAll().stream()
                .filter(vehicle -> "UNDER_MAINTENANCE".equalsIgnoreCase(nullToEmpty(vehicle.getVehicleStatus())))
                .count();

        setHeader("Xin chào, " + displayName,
                "Theo dõi kế hoạch bảo dưỡng, phiếu sửa chữa và lịch sử xử lý xe.",
                "TECH");
        setKpis("Đang bảo dưỡng", underMaintenance, "Lấy từ trạng thái phương tiện",
                "Kế hoạch", "-", "Chờ module bảo dưỡng kết nối",
                "Phiếu xử lý", "-", "Chờ module sửa chữa kết nối",
                "Hoàn thành", "-", "Chờ thống kê công việc kết nối");
        setModules("Kế hoạch bảo dưỡng", "Lập lịch theo ngày hoặc số km cho từng phương tiện.", "PLANNING",
                "Cập nhật bảo dưỡng", "Ghi nhận nội dung sửa chữa, ODO và chi phí.", "WORK ORDER",
                "Lịch sử bảo dưỡng", "Tra cứu các lần bảo dưỡng theo từng xe.", "HISTORY",
                null, null, null);
        setFocus("Ưu tiên kỹ thuật",
                "Kiểm tra xe đang bảo dưỡng và chuẩn bị nối dữ liệu cho kế hoạch, phiếu sửa chữa, lịch sử.",
                "1. Xem xe đang ở trạng thái bảo dưỡng.",
                "2. Cập nhật phiếu khi module kỹ thuật sẵn sàng.",
                "3. Đối chiếu lịch sử bảo dưỡng theo biển số.");
    }

    private void renderDefault(String displayName) {
        setHeader("Xin chào, " + displayName, "Tài khoản chưa có vai trò hợp lệ.", "UNKNOWN");
        setKpis("Phương tiện", vehicleDAO.findAll().size(), "Dữ liệu tổng quan",
                "Sắp hết hạn", documentAlertDAO.countByStatus("COMING_DUE"), "Cảnh báo giấy tờ",
                "Đã hết hạn", documentAlertDAO.countByStatus("OVERDUE"), "Cảnh báo giấy tờ",
                "Vai trò", roleDAO.findAllActive().size(), "Role đang hoạt động");
        setModules("Dashboard", "Liên hệ quản trị để kiểm tra vai trò tài khoản.", "ACCOUNT",
                null, null, null,
                null, null, null,
                null, null, null);
        setFocus("Trạng thái tài khoản", "Chưa xác định được quyền hiển thị chức năng.",
                "1. Kiểm tra bảng users.", "2. Kiểm tra role_id.", "3. Đăng nhập lại sau khi sửa role.");
    }

    private void setHeader(String title, String subtitle, String badge) {
        lblTitle.setText(title);
        lblSubtitle.setText(subtitle);
        lblRoleBadge.setText(badge);
    }

    private void setKpis(String t1, Object v1, String h1,
                         String t2, Object v2, String h2,
                         String t3, Object v3, String h3,
                         String t4, Object v4, String h4) {
        lblKpiOneTitle.setText(t1);
        lblKpiOneValue.setText(String.valueOf(v1));
        lblKpiOneHint.setText(h1);
        lblKpiTwoTitle.setText(t2);
        lblKpiTwoValue.setText(String.valueOf(v2));
        lblKpiTwoHint.setText(h2);
        lblKpiThreeTitle.setText(t3);
        lblKpiThreeValue.setText(String.valueOf(v3));
        lblKpiThreeHint.setText(h3);
        lblKpiFourTitle.setText(t4);
        lblKpiFourValue.setText(String.valueOf(v4));
        lblKpiFourHint.setText(h4);
    }

    private void setModules(String t1, String x1, String b1,
                            String t2, String x2, String b2,
                            String t3, String x3, String b3,
                            String t4, String x4, String b4) {
        setModule(lblModuleOneTitle, lblModuleOneText, lblModuleOneBadge, t1, x1, b1);
        setOptionalModule(lblModuleTwoTitle, lblModuleTwoText, lblModuleTwoBadge, t2, x2, b2);
        setOptionalModule(lblModuleThreeTitle, lblModuleThreeText, lblModuleThreeBadge, t3, x3, b3);
        if (t4 == null) {
            moduleFour.setVisible(false);
            moduleFour.setManaged(false);
        } else {
            moduleFour.setVisible(true);
            moduleFour.setManaged(true);
            setModule(lblModuleFourTitle, lblModuleFourText, lblModuleFourBadge, t4, x4, b4);
        }
    }

    private void setOptionalModule(Label titleLabel, Label textLabel, Label badgeLabel, String title, String text, String badge) {
        if (title == null) {
            titleLabel.getParent().setVisible(false);
            titleLabel.getParent().setManaged(false);
            return;
        }
        titleLabel.getParent().setVisible(true);
        titleLabel.getParent().setManaged(true);
        setModule(titleLabel, textLabel, badgeLabel, title, text, badge);
    }

    private void setModule(Label titleLabel, Label textLabel, Label badgeLabel, String title, String text, String badge) {
        titleLabel.setText(title);
        textLabel.setText(text);
        badgeLabel.setText(badge);
    }

    private void setFocus(String title, String text, String checkOne, String checkTwo, String checkThree) {
        lblFocusTitle.setText(title);
        lblFocusText.setText(text);
        lblCheckOne.setText(checkOne);
        lblCheckTwo.setText(checkTwo);
        lblCheckThree.setText(checkThree);
    }

    private String normalizeRoleCode(String roleCode) {
        if (roleCode == null || roleCode.isBlank()) {
            return "";
        }
        String normalized = roleCode.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "FLEET_MANAGER" -> "MANAGER";
            case "TECHNICIAN" -> "TECH";
            default -> normalized;
        };
    }

    private String resolveDisplayName(User user) {
        if (user == null) {
            return "người dùng";
        }
        if (user.getFullName() != null && !user.getFullName().isBlank()) {
            return user.getFullName().trim();
        }
        if (user.getUsername() != null && !user.getUsername().isBlank()) {
            return user.getUsername().trim();
        }
        return "người dùng";
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
