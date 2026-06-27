# ⌨️ AutoType Keyboard

کیبوردی که متن ذخیره‌شده را خودکار تایپ می‌کند.

---

## ✨ ویژگی‌ها
- بدون سایز (wrap_content) - فقط دکمه‌های کنترلی
- ذخیره متن دلخواه
- ارسال یکجا و فوری متن
- حلقه خودکار: بعد از هر Enter، دوباره متن را می‌نویسد
- دکمه خاموش برای توقف

---

## 📱 نصب در Termux

### مرحله ۱ - آماده‌سازی Termux
```bash
pkg update && pkg upgrade -y
pkg install -y openjdk-17 gradle
```

### مرحله ۲ - انتقال فایل‌ها
فایل ZIP پروژه را دانلود کرده و در Termux:
```bash
cp /sdcard/Download/AutoTypeKeyboard.zip ~/
cd ~
unzip AutoTypeKeyboard.zip
cd AutoTypeKeyboard
```

### مرحله ۳ - بیلد APK
```bash
gradle assembleDebug
```
(اولین بار ممکن است ۵-۱۰ دقیقه طول بکشد)

### مرحله ۴ - نصب APK
```bash
# مسیر APK:
# app/build/outputs/apk/debug/app-debug.apk

# کپی به دانلودز:
cp app/build/outputs/apk/debug/app-debug.apk /sdcard/Download/AutoTypeKeyboard.apk
```
سپس APK را از فایل منیجر نصب کنید.

---

## 🔧 مراحل فعال‌سازی کیبورد
1. اپ AutoType را باز کنید
2. متن خود را وارد و ذخیره کنید
3. دکمه «فعال‌سازی» را بزنید → در تنظیمات AutoType را فعال کنید
4. دکمه «انتخاب کیبورد» را بزنید → AutoType را انتخاب کنید
5. در هر اپی که باز است، کیبورد AutoType ظاهر می‌شود
6. دکمه **شروع** را بزنید
7. بعد از هر Enter، خودکار دوباره متن را می‌نویسد
8. دکمه **خاموش** برای توقف

---

## ⚙️ ساختار پروژه
```
AutoTypeKeyboard/
├── app/
│   └── src/main/
│       ├── java/com/autotype/keyboard/
│       │   ├── AutoTypeIME.java     ← سرویس کیبورد
│       │   └── SettingsActivity.java ← صفحه تنظیمات
│       ├── res/
│       │   ├── layout/
│       │   │   ├── keyboard_view.xml
│       │   │   └── activity_settings.xml
│       │   ├── xml/method.xml
│       │   └── values/
│       └── AndroidManifest.xml
├── build.gradle
├── settings.gradle
└── gradlew
```
