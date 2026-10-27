# AIPack — All AI in one app

دایرکتوری ابزارهای هوش مصنوعی برای اندروید. کاربر دسته را انتخاب می‌کند، کارت ابزارها را با برچسب قیمت و وضعیت دسترسی از ایران می‌بیند و با یک ضربه سایت ابزار در مرورگر باز می‌شود.

- Kotlin + Jetpack Compose، بدون هیچ سرویس خارجی و بدون مجوز اینترنت
- لیست ابزارها فقط در یک فایل: `app/src/main/assets/tools.json`
- رابط انگلیسی؛ متن‌ها در `strings.xml` (آماده برای افزودن فارسی)

> ⚠️ **وضعیت صادقانه نسخه اول:** این پروژه در محیطی نوشته شد که Android SDK نداشت، پس **هنوز کامپایل نشده و APK آن ساخته نشده**. فقط داده (`tools.json`)، XML و Workflow اعتبارسنجی شده‌اند. اولین Build واقعی روی GitHub Actions انجام می‌شود. اگر قرمز شد، لاگ را برای رفع مشکل بفرست (پایین را ببین).

## ۱) گذاشتن پروژه در GitHub

1. در GitHub یک مخزن خالی بساز (مثلاً `aipack`).
2. محتوای این پوشه را در آن آپلود کن (با Git یا دکمه *Add file → Upload files*). پوشه مخفی `.github` و فایل `gradlew` حتماً آپلود شوند.
3. اگر دسترسی به GitHub از ایران مشکل داشت، با VPN انجام بده؛ وضعیت فعلی را من تایید نکرده‌ام.

## ۲) گرفتن APK

1. در مخزن به تب **Actions** برو. با هر Push روی شاخه `main` (یا با دکمه *Run workflow*) ساخت شروع می‌شود.
2. وقتی سبز شد، وارد همان اجرا شو و از پایین صفحه **Artifacts → AIPack-debug-apk** را دانلود کن (یک zip که APK داخلش است).
3. APK را روی گوشی کپی کن و نصب کن (باید «نصب از منابع ناشناس» برای مرورگر/فایل‌منیجر روشن باشد).

این APK از نوع **Debug** است: برای تست و نصب مستقیم خوب است، ولی برای کافه‌بازار مناسب نیست.

## ۳) انتشار نسخه امضاشده (Release)

برای کافه‌بازار، مایکت و آپدیت‌های بعدی، APK باید با یک Keystore **ثابت** امضا شود.

1. ساخت Keystore (فقط یک بار):
   ```
   keytool -genkeypair -v -keystore aipack-release.jks -alias aipack -keyalg RSA -keysize 2048 -validity 10000
   ```
2. **از این فایل و رمزهایش چند نسخه پشتیبان امن بگیر.** اگر گم شود، دیگر نمی‌توانی آپدیت بدهی. هرگز آن را در مخزن نگذار.
3. تبدیل به base64 (لینوکس/مک: `base64 -w0 aipack-release.jks` ؛ ویندوز PowerShell: `[Convert]::ToBase64String([IO.File]::ReadAllBytes("aipack-release.jks"))`).
4. در GitHub: *Settings → Secrets and variables → Actions* چهار Secret بساز:
   `KEYSTORE_BASE64` (خروجی مرحله قبل)، `KEYSTORE_PASSWORD`، `KEY_ALIAS` (مثلاً aipack)، `KEY_PASSWORD`
5. یک Tag بزن تا نسخه منتشر شود:
   ```
   git tag v1.0.0
   git push origin v1.0.0
   ```
   APK امضاشده در بخش **Releases** می‌آید. اگر Secretها نباشند، فقط یک Pre-release با APK **DEBUG** ساخته می‌شود (نامش `-DEBUG` دارد).

## ۴) قبل از انتشار عمومی حتماً

- **`applicationId` را عوض کن** (`app/build.gradle.kts`، الان `com.aipack.app` است و موقتی است). مثلاً `io.github.<username>.aipack`. بعد از انتشار قابل تغییر نیست. اگر عوضش کردی، پوشه‌های `com/aipack/app` و `namespace` را هم هماهنگ نگه دار (یا فقط `applicationId` را عوض کن؛ `namespace` می‌تواند همان بماند).
- ستون **دسترسی از ایران** را تست و در `tools.json` پر کن (`iran_access`). الان همه Unverified هستند و حدس‌ها با «(est.)» نشان داده می‌شوند.
- `verified` و `last_checked` را بعد از بررسی هر ابزار بروز کن.
- اسم «AIPack» را در کافه‌بازار/مایکت جستجو کن (پروژه‌ای متن‌باز با اسم AIPACK هم وجود دارد).
- برای کافه‌بازار: APK امضاشده، آیکون و توضیحات فارسی، اسکرین‌شات و یک صفحه سیاست حریم خصوصی لازم است.

## ۵) ویرایش لیست ابزارها
راهنما: [`docs/how-to-add-a-tool.md`](docs/how-to-add-a-tool.md) — فهرست دسته‌ها: [`docs/categories.md`](docs/categories.md)

## ساختار
```
.github/workflows/android.yml   ساخت و تست خودکار، Artifact، Release
app/src/main/assets/tools.json  لیست ابزارها و دسته‌ها
app/src/main/java/...           data (parser)، ui (صفحه‌ها و کارت‌ها)
app/src/test/...                تست parser و اعتبارسنجی tools.json
```

## اگر Build قرمز شد
در Actions وارد اجرای ناموفق شو، لاگ مرحله‌ای که قرمز است را کپی کن و بفرست. نسخه‌ها (Gradle 8.10.2، AGP 8.7.3، Kotlin 2.0.21، Compose BOM 2024.12.01) عمداً سازگار و آزموده انتخاب شده‌اند، نه جدیدترین؛ ارتقا بعد از اولین Build سبز راحت‌تر است.
