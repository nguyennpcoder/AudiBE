# Fonts cho PDF tiếng Việt

Thư mục này chứa các font để hỗ trợ tiếng Việt trong PDF export.

## Fonts có sẵn:

1. **arial-unicode.ttf** - Font Arial Unicode MS (hỗ trợ tiếng Việt hoàn chỉnh)
2. **times-new-roman.ttf** - Font Times New Roman (hỗ trợ tiếng Việt hoàn chỉnh)
3. **arial.ttc** - Font Arial (TrueType Collection - backup)
4. **times.ttc** - Font Times (TrueType Collection - backup)

## Cách sử dụng:

Các font này sẽ được tự động sử dụng trong `CauHinhTuyChinhService.java` khi tạo PDF.

## Lưu ý:

- Font .ttf là TrueType, hỗ trợ nhiều ký tự Unicode
- Font .ttc là TrueType Collection, có thể gặp vấn đề với encoding Identity-H
- Sử dụng encoding `BaseFont.IDENTITY_H` để hỗ trợ tiếng Việt
- Font được nhúng vào PDF để đảm bảo hiển thị đúng trên mọi thiết bị

## Thứ tự ưu tiên:

1. Arial Unicode.ttf (font chính)
2. Times New Roman.ttf (font backup)
3. Font hệ thống
4. Font mặc định 