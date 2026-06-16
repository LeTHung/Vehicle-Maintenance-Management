# Kịch bản demo — Module Bảo dưỡng & Báo cáo

Áp dụng cho phần demo bước 9–13 (Nguyễn Quang Huy). Dữ liệu dựa trên `data/seed-demo-realistic.sql`.

## 0. Chuẩn bị trước khi demo

1. Nạp DB sạch (khuyến nghị để dữ liệu demo gọn, không lẫn dữ liệu test cũ):
   ```powershell
   Get-Content data\Dump20260524.sql        | mysql -u root -p
   Get-Content data\seed-auth.sql           | mysql -u root -p
   Get-Content data\seed-demo-realistic.sql | mysql -u root -p
   ```
2. Chạy app:
   ```powershell
   .\mvnw javafx:run
   ```
3. Tài khoản: `manager/123456` (Quản lý đội xe), `tech/123456` (Kỹ thuật viên).

**Số liệu chốt sẵn sau khi seed** (để biết kỳ vọng):
- 6 xe: `51C-256.89`, `50H-112.35`, `51B-345.67`, `60C-990.12`, `51F-888.66`, `51LD-123.45`.
- Cảnh báo bảo dưỡng: có cả xe **quá hạn** và **sắp đến hạn**.
- 5 phiếu COMPLETED → báo cáo có số liệu, ví dụ: `51C-256.89` ~705.000đ, `51B-345.67` ~2.380.000đ, `51F-888.66` ~280.000đ.

---

## Kịch bản 5 bước

### Bước 1 — Quản lý lập kế hoạch bảo dưỡng *(login `manager`)*
- Menu **Kế hoạch bảo dưỡng**.
- Chọn xe `60C-990.12`, loại **Thay dầu**, **Chu kỳ ngày = 90**, **Ngày bảo dưỡng cuối = hôm nay − 100 ngày** (chọn 1 ngày quá khứ), bỏ trống "Ngày đến hạn".
- Nhấn **Lưu**.
- **Điểm nhấn nói:** "Hệ thống *tự tính* ngày đến hạn = ngày bảo dưỡng cuối + chu kỳ. Ngưỡng cảnh báo trước lấy mặc định từ cấu hình nếu để trống."
- **Kỳ vọng:** dòng mới xuất hiện trong bảng, cột "Ngày đến hạn" đã được tính tự động. Thử Lưu lại cùng xe + loại → báo lỗi *trùng kế hoạch đang hoạt động*.

### Bước 2 — Kỹ thuật viên xem xe cần bảo dưỡng *(login `tech`)*
- Logout, login `tech`. Menu **Xe cần bảo dưỡng**.
- **Kỳ vọng:** bảng liệt kê xe **Quá hạn** + **Sắp đến hạn**; 2 thẻ tóm tắt đếm số lượng. Đổi bộ lọc "Quá hạn" / "Sắp đến hạn" → bảng lọc tương ứng.
- **Điểm nhấn nói:** "Danh sách lấy từ view nghiệp vụ, tự phân loại theo ngày và theo số km (ODO) so với cấu hình cảnh báo."

### Bước 3 — Kỹ thuật viên tạo phiếu + nhập phụ tùng *(vẫn `tech`)*
- Menu **Cập nhật bảo dưỡng**.
- Chọn xe `60C-990.12`, Loại phiếu **Sửa chữa phát sinh**, Trạng thái **Hoàn thành**, Ngày thực hiện = hôm nay, ODO `32000`, Nội dung "Thay dầu + kiểm tra phanh".
- Khu **Hạng mục / Phụ tùng**:
  - `WORK`, "Công thay dầu", SL `1`, đơn giá `150000` → **+ Thêm**.
  - `PART`, "Dầu động cơ 10W-40 4L", SL `1`, đơn giá `420000` → **+ Thêm**.
- Nhấn **Lưu phiếu**.
- **Điểm nhấn nói:** "Thành tiền từng hạng mục tự tính; phiếu để trạng thái Hoàn thành nên sẽ vào báo cáo chi phí."
- **Kỳ vọng:** thông báo *"Đã lưu phiếu bảo dưỡng (2 hạng mục)"*; phiếu xuất hiện trong bảng, cột Kỹ thuật viên hiện tên người đăng nhập.

### Bước 4 — Xem lịch sử bảo dưỡng theo xe *(`tech` hoặc `manager`)*
- Menu **Lịch sử bảo dưỡng** (cả KTV và Quản lý đều mở được).
- Chọn xe `51B-345.67` → bảng trên hiện các phiếu của xe.
- **Click một phiếu COMPLETED** → bảng dưới "Chi tiết hạng mục / phụ tùng" hiện các dòng line items.
- **Điểm nhấn nói:** "Tra cứu hồ sơ sửa chữa theo từng xe; chọn phiếu để xem chi tiết phụ tùng đã dùng."
- **Kỳ vọng:** xe `51B-345.67` có phiếu "Bảo dưỡng định kỳ xe khách 95.000 km" với nhiều hạng mục.

### Bước 5 — Quản lý xem báo cáo chi phí *(login `manager`)*
- Logout, login `manager`. Menu **Báo cáo chi phí**.
- Chọn năm `2026` → **Tìm kiếm**.
- **Kỳ vọng:** bảng hiện chi phí theo xe/tháng; 3 ô tổng cập nhật. Lọc theo 1 xe → chỉ còn dòng của xe đó.
- **Điểm nhấn nói:** "Báo cáo tổng hợp từ các phiếu đã hoàn thành (COMPLETED), nhóm theo xe và theo tháng."

---

## Kiểm tra phân quyền (nếu giám khảo hỏi)
- `tech` **không** thấy Kế hoạch / Báo cáo (bấm → "Không có quyền").
- `tech` **thấy** Lịch sử bảo dưỡng (đã được cấp quyền tra cứu hồ sơ theo xe).
- `admin` **không** thấy menu Bảo dưỡng/Báo cáo.

## Lưu ý khi demo
- **Báo cáo chỉ tính phiếu COMPLETED** — nếu phiếu tạo ở bước 3 để trạng thái khác thì sẽ không vào báo cáo.
- Nếu lập kế hoạch mà để trống cả "Ngày bảo dưỡng cuối" lẫn "Ngày đến hạn" → hệ thống báo lỗi bắt buộc nhập (đúng thiết kế).
- Kế hoạch có ngày đến hạn còn xa sẽ KHÔNG hiện trong Cảnh báo (trạng thái NORMAL) — đây là đúng nghiệp vụ, không phải lỗi.
