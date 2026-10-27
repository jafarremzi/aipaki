# چطور یک ابزار اضافه یا حذف کنم؟

همه ابزارها فقط در یک فایل هستند: `app/src/main/assets/tools.json`

## اضافه کردن
یک آبجکت جدید به آرایه `tools` اضافه کن (مثال):

```json
{
  "id": "my-tool",
  "name": "My Tool",
  "description": "One short line about what it does.",
  "url": "https://example.com",
  "categories": [ { "category": "image", "sub": "Generate" } ],
  "pricing": "Freemium",
  "iran_access": "Unverified",
  "iran_access_guess": "VPN",
  "persian_support": "Unverified",
  "verified": false
}
```

- `id` باید یکتا باشد (حروف کوچک انگلیسی و خط تیره).
- `category` باید یکی از id دسته‌ها باشد و `sub` یکی از زیردسته‌های همان دسته (بخش `categories` بالای فایل).
- یک ابزار می‌تواند چند دسته داشته باشد: چند آیتم در `categories`.
- `pricing`: `Free` | `Freemium` | `Paid`
- `iran_access`: `Open` | `VPN` | `Varies` | `Unverified` (فقط بعد از تست واقعی از ایران چیزی غیر از Unverified بگذار؛ تا آن موقع حدس را در `iran_access_guess` بنویس)
- `persian_support`: `Yes` | `Partial` | `No` | `Unverified`

فیلدهای اختیاری: `best_for`, `free_tier_type`, `watermark_on_free`, `commercial_use_on_free`, `type` (`Native`/`Gateway`/`Original`), `signup`, `payment`, `last_checked`, `note`.

## حذف
آبجکت ابزار را از آرایه پاک کن.

## بعد از تغییر
- Commit و Push کن. Workflow تست‌ها را اجرا می‌کند و اگر فایل ایراد داشته باشد (id تکراری، دسته ناموجود، لینک غیر https) Build قرمز می‌شود.
- قیمت دقیق یا مقدار سهمیه رایگان را ننویس؛ این‌ها زود عوض می‌شوند.
