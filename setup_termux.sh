#!/bin/bash
# =============================================
# AutoType Keyboard - اسکریپت نصب در Termux
# =============================================

echo ""
echo "🚀 شروع ساخت AutoType Keyboard..."
echo "======================================"

# بررسی Java
if ! command -v javac &> /dev/null; then
    echo "📦 نصب Java..."
    pkg install -y openjdk-17
fi

# بررسی اتصال اینترنت
echo "📡 بررسی اینترنت..."

# نصب Gradle اگر نداریم
if ! command -v gradle &> /dev/null; then
    echo "📦 نصب Gradle..."
    pkg install -y gradle
fi

echo ""
echo "✅ محیط آماده است!"
echo ""
echo "📁 پوشه پروژه را از کامپیوتر یا Claude به Termux منتقل کنید:"
echo "   مسیر پیشنهادی: ~/AutoTypeKeyboard/"
echo ""
echo "سپس دستورات زیر را اجرا کنید:"
echo ""
echo "  cd ~/AutoTypeKeyboard"
echo "  chmod +x gradlew"
echo "  ./gradlew assembleDebug"
echo ""
echo "APK در این مسیر ساخته می‌شود:"
echo "  app/build/outputs/apk/debug/app-debug.apk"
echo ""
echo "برای نصب مستقیم:"
echo "  adb install app/build/outputs/apk/debug/app-debug.apk"
echo "  یا فایل APK را در فایل منیجر باز کنید"
