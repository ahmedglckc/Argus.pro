#pragma warning(disable : 4996)
#include <windows.h>
#include <iostream>
#include <thread>
#include <future>
#include <vector>
#include <string>
#include <mutex>
#include <fstream>
#include <map>
#include <set>
#include <filesystem>
#include "Dependencies/lib/JNI/jni.h"
#include "Dependencies/include/MinHook.h"
#include "Dependencies/lib/JNI/jvmti.h"

// Global değişkenler
JavaVM* jvm = nullptr;
JNIEnv* env = nullptr;
jvmtiEnv* jvmti = nullptr;
std::mutex console_mutex; // Konsol yazımı için mutex

// glOrtho hook için typedef
typedef void(__stdcall* p_glOrtho)(double, double, double, double, double, double);
p_glOrtho o_glOrtho = nullptr;

#define XORSTR(str) (str)

namespace Bypass {
    void* ResolveDynamicAddress(HMODULE module, const char* functionName) {
        if (!module || !functionName) return nullptr;
        return reinterpret_cast<void*>(GetProcAddress(module, functionName));
    }

    HMODULE SpoofModuleLoad(const char* moduleName) {
        if (!moduleName) return nullptr;
        return LoadLibraryA(moduleName);
    }
}

namespace JarLoader {
    // Optimize edilmiş dosya okuma
    std::vector<std::uint8_t> getBytes(const std::string& file_path) {
        std::ifstream file(file_path, std::ios::binary | std::ios::ate);
        if (!file) {
            std::lock_guard<std::mutex> lock(console_mutex);
            std::cerr << "[!] Dosya açılamadı: " << file_path << "\n";
            return {};
        }

        size_t file_size = static_cast<size_t>(file.tellg());
        file.seekg(0, std::ios::beg);

        std::vector<std::uint8_t> buffer(file_size);
        file.read(reinterpret_cast<char*>(buffer.data()), file_size);
        file.close();

        return buffer;
    }

    // JAR yükleme fonksiyonu
    bool load_jar(JNIEnv* env, const std::string& jar_file_path, bool ignore_exceptions = true) {
        if (!env) {
            std::lock_guard<std::mutex> lock(console_mutex);
            std::cerr << "[!] JNIEnv null!" << std::endl;
            return false;
        }

        std::lock_guard<std::mutex> lock(console_mutex);
        std::cout << "[*] JAR yükleniyor: " << jar_file_path << std::endl;

        auto jar_data = getBytes(jar_file_path);
        if (jar_data.empty()) {
            std::cerr << "[!] JAR dosyası boş veya okunamadı: " << jar_file_path << "\n";
            return false;
        }

        std::map<std::string, std::vector<std::uint8_t>> class_buffers;
        std::set<std::string> defined_classes;

        auto string_replace_all = [](std::string str, const std::string& from, const std::string& to) -> std::string {
            size_t start_pos = 0;
            while ((start_pos = str.find(from, start_pos)) != std::string::npos) {
                str.replace(start_pos, from.length(), to);
                start_pos += to.length();
            }
            return str;
            };

        auto byte_array_input_stream = [](JNIEnv* env, const std::vector<std::uint8_t>& buffer) -> jobject {
            jbyteArray arr = env->NewByteArray(static_cast<jsize>(buffer.size()));
            if (!arr) return nullptr;
            env->SetByteArrayRegion(arr, 0, static_cast<jsize>(buffer.size()), reinterpret_cast<const jbyte*>(buffer.data()));
            jclass cls = env->FindClass("java/io/ByteArrayInputStream");
            if (!cls) {
                env->DeleteLocalRef(arr);
                return nullptr;
            }
            jmethodID method = env->GetMethodID(cls, "<init>", "([B)V");
            if (!method) {
                env->DeleteLocalRef(cls);
                env->DeleteLocalRef(arr);
                return nullptr;
            }
            jobject result = env->NewObject(cls, method, arr);
            env->DeleteLocalRef(cls);
            env->DeleteLocalRef(arr);
            return result ? env->NewGlobalRef(result) : nullptr;
            };

        auto jar_input_stream = [](JNIEnv* env, jobject input_stream) -> jobject {
            jclass cls = env->FindClass("java/util/jar/JarInputStream");
            if (!cls) return nullptr;
            jmethodID method = env->GetMethodID(cls, "<init>", "(Ljava/io/InputStream;)V");
            if (!method) {
                env->DeleteLocalRef(cls);
                return nullptr;
            }
            jobject result = env->NewObject(cls, method, input_stream);
            env->DeleteLocalRef(cls);
            return result ? env->NewGlobalRef(result) : nullptr;
            };

        auto input_stream_read = [](JNIEnv* env, jobject input_stream) -> jint {
            jclass cls = env->GetObjectClass(input_stream);
            if (!cls) return -1;
            jmethodID method = env->GetMethodID(cls, "read", "()I");
            if (!method) {
                env->DeleteLocalRef(cls);
                return -1;
            }
            jint result = env->CallIntMethod(input_stream, method);
            env->DeleteLocalRef(cls);
            return result;
            };

        auto byte_array_output_stream = [](JNIEnv* env) -> jobject {
            jclass cls = env->FindClass("java/io/ByteArrayOutputStream");
            if (!cls) return nullptr;
            jmethodID method = env->GetMethodID(cls, "<init>", "()V");
            if (!method) {
                env->DeleteLocalRef(cls);
                return nullptr;
            }
            jobject result = env->NewObject(cls, method);
            env->DeleteLocalRef(cls);
            return result ? env->NewGlobalRef(result) : nullptr;
            };

        auto output_stream_write = [](JNIEnv* env, jobject output_stream, jint value) {
            jclass cls = env->GetObjectClass(output_stream);
            if (!cls) return;
            jmethodID method = env->GetMethodID(cls, "write", "(I)V");
            if (!method) {
                env->DeleteLocalRef(cls);
                return;
            }
            env->CallVoidMethod(output_stream, method, value);
            env->DeleteLocalRef(cls);
            };

        auto byte_array_output_stream_to_byte_array = [](JNIEnv* env, jobject output_stream) -> std::vector<std::uint8_t> {
            jclass cls = env->GetObjectClass(output_stream);
            if (!cls) return {};
            jmethodID method = env->GetMethodID(cls, "toByteArray", "()[B");
            if (!method) {
                env->DeleteLocalRef(cls);
                return {};
            }
            jbyteArray bytes = reinterpret_cast<jbyteArray>(env->CallObjectMethod(output_stream, method));
            if (!bytes) {
                env->DeleteLocalRef(cls);
                return {};
            }
            jsize size = env->GetArrayLength(bytes);
            std::vector<std::uint8_t> result(size);
            env->GetByteArrayRegion(bytes, 0, size, reinterpret_cast<jbyte*>(result.data()));
            env->DeleteLocalRef(bytes);
            env->DeleteLocalRef(cls);
            return result;
            };

        auto get_next_jar_entry = [](JNIEnv* env, jobject jar_input_stream) -> jobject {
            jclass cls = env->GetObjectClass(jar_input_stream);
            if (!cls) return nullptr;
            jmethodID method = env->GetMethodID(cls, "getNextJarEntry", "()Ljava/util/jar/JarEntry;");
            if (!method) {
                env->DeleteLocalRef(cls);
                return nullptr;
            }
            jobject result = env->CallObjectMethod(jar_input_stream, method);
            env->DeleteLocalRef(cls);
            return result ? env->NewGlobalRef(result) : nullptr;
            };

        auto jar_entry_get_name = [](JNIEnv* env, jobject jar_entry) -> std::string {
            jclass cls = env->FindClass("java/util/jar/JarEntry");
            if (!cls) return "";
            jmethodID method = env->GetMethodID(cls, "getName", "()Ljava/lang/String;");
            if (!method) {
                env->DeleteLocalRef(cls);
                return "";
            }
            jstring jstr = reinterpret_cast<jstring>(env->CallObjectMethod(jar_entry, method));
            if (!jstr) {
                env->DeleteLocalRef(cls);
                return "";
            }
            const char* name_string = env->GetStringUTFChars(jstr, nullptr);
            std::string result = name_string ? name_string : "";
            env->ReleaseStringUTFChars(jstr, name_string);
            env->DeleteLocalRef(jstr);
            env->DeleteLocalRef(cls);
            return result;
            };

        jobject bais = byte_array_input_stream(env, jar_data);
        if (!bais) {
            std::cerr << "[!] ByteArrayInputStream açılamadı\n";
            return false;
        }

        jobject jis = jar_input_stream(env, bais);
        if (!jis) {
            env->DeleteGlobalRef(bais);
            std::cerr << "[!] JarInputStream açılamadı\n";
            return false;
        }

        const std::string extension = ".class";
        while (jobject jar_entry = get_next_jar_entry(env, jis)) {
            std::string name = jar_entry_get_name(env, jar_entry);
            if (name.length() > extension.length() && name.rfind(extension) == name.length() - extension.length()) {
                jobject baos = byte_array_output_stream(env);
                if (!baos) {
                    std::cerr << "[!] ByteArrayOutputStream açılamadı: " << name << "\n";
                    env->DeleteGlobalRef(jar_entry);
                    if (!ignore_exceptions) {
                        env->DeleteGlobalRef(jis);
                        env->DeleteGlobalRef(bais);
                        return false;
                    }
                    continue;
                }

                jint value = -1;
                while ((value = input_stream_read(env, jis)) != -1) {
                    output_stream_write(env, baos, value);
                }

                std::vector<std::uint8_t> bytes = byte_array_output_stream_to_byte_array(env, baos);
                class_buffers[name] = bytes;
                env->DeleteGlobalRef(baos);
            }
            else {
                std::cout << "[*] Kaynak atlanıyor: " << name << "\n";
            }
            env->DeleteGlobalRef(jar_entry);
        }

        env->DeleteGlobalRef(jis);
        env->DeleteGlobalRef(bais);

        for (const auto& [name, bytes] : class_buffers) {
            std::string canonicalName = string_replace_all(string_replace_all(name, "/", "."), ".class", "");
            jbyteArray byteArray = env->NewByteArray(static_cast<jsize>(bytes.size()));
            if (!byteArray) {
                std::cerr << "[!] ByteArray oluşturulamadı: " << canonicalName << "\n";
                if (!ignore_exceptions) return false;
                continue;
            }

            env->SetByteArrayRegion(byteArray, 0, static_cast<jsize>(bytes.size()), reinterpret_cast<const jbyte*>(bytes.data()));
            jbyte* byteArrayElements = env->GetByteArrayElements(byteArray, nullptr);
            if (!byteArrayElements) {
                std::cerr << "[!] ByteArray elemanları alınamadı: " << canonicalName << "\n";
                env->DeleteLocalRef(byteArray);
                if (!ignore_exceptions) return false;
                continue;
            }

            jclass cls = env->DefineClass(string_replace_all(name, ".class", "").c_str(), nullptr, byteArrayElements, static_cast<jsize>(bytes.size()));
            env->ReleaseByteArrayElements(byteArray, byteArrayElements, JNI_ABORT);
            env->DeleteLocalRef(byteArray);

            if (cls) {
                std::cout << "[*] Tanımlandı: " << canonicalName << " Boyut: " << bytes.size() << "\n";
                defined_classes.insert(canonicalName);
                env->DeleteLocalRef(cls);
            }
            else {
                std::cerr << "[!] Tanımlama başarısız: " << canonicalName << "\n";
                if (env->ExceptionCheck()) {
                    env->ExceptionDescribe();
                    env->ExceptionClear();
                }
                if (!ignore_exceptions) return false;
            }
        }

        return true;
    }
}

// Asenkron JAR yükleme
void InjectAsync() {
    std::vector<std::string> jar_files = {
        "C:\\Wentra\\libraries\\byte-buddy-1.17.5.jar",
        "C:\\Wentra\\libraries\\cache2k-api-2.2.1.Final.jar",
        "C:\\Wentra\\libraries\\cache2k-core-2.2.1.Final.jar",
        "C:\\Wentra\\libraries\\codecjorbis-20101023.jar",
        "C:\\Wentra\\libraries\\codecwav-20101023.jar",
        "C:\\Wentra\\libraries\\commons-codec-1.9.jar",
        "C:\\Wentra\\libraries\\commons-compress-1.8.1.jar",
        "C:\\Wentra\\libraries\\commons-io-2.4.jar",
        "C:\\Wentra\\libraries\\commons-lang3-3.3.2.jar",
        "C:\\Wentra\\libraries\\commons-logging-1.2.jar",
        "C:\\Wentra\\libraries\\discord-rpc.jar",
        "C:\\Wentra\\libraries\\fastutil-8.5.6.jar",
        "C:\\Wentra\\libraries\\filters-2.0.235.jar",
        "C:\\Wentra\\libraries\\gson-2.2.4.jar",
        "C:\\Wentra\\libraries\\guava-17.0.jar",
        "C:\\Wentra\\libraries\\httpclient-4.5.13.jar",
        "C:\\Wentra\\libraries\\httpcore-4.4.15.jar",
        "C:\\Wentra\\libraries\\icu4j-51.2.jar",
        "C:\\Wentra\\libraries\\jinput-2.0.5.jar",
        "C:\\Wentra\\libraries\\jinput-platform-2.0.5-natives-osx.jar",
        "C:\\Wentra\\libraries\\jinput-platform-2.0.5-natives-windows.jar",
        "C:\\Wentra\\libraries\\jna-4.5.0.jar",
        "C:\\Wentra\\libraries\\jopt-simple-4.6.jar",
        "C:\\Wentra\\libraries\\jutils-1.0.0.jar",
        "C:\\Wentra\\libraries\\libraryjavasound-20101123.jar",
        "C:\\Wentra\\libraries\\librarylwjglopenal-20100824.jar",
        "C:\\Wentra\\libraries\\log4j-api-2.17.2.jar",
        "C:\\Wentra\\libraries\\log4j-core-2.17.2.jar",
        "C:\\Wentra\\libraries\\lwjgl-2.9.2-nightly-20140822-osx.jar",
        "C:\\Wentra\\libraries\\lwjgl-2.9.3-linux.jar",
        "C:\\Wentra\\libraries\\lwjgl-2.9.4-nightly-20150209-windows.jar",
        "C:\\Wentra\\libraries\\lwjgl-platform-2.9.2-nightly-20140822-natives-osx.jar",
        "C:\\Wentra\\libraries\\lwjgl-platform-2.9.3-natives-linux.jar",
        "C:\\Wentra\\libraries\\lwjgl-platform-2.9.4-nightly-20150209-natives-windows.jar",
        "C:\\Wentra\\libraries\\lwjgl-platform-2.9.4-nightly-20150209-windows.jar",
        "C:\\Wentra\\libraries\\lwjgl_util-2.9.2-nightly-20140822-osx.jar",
        "C:\\Wentra\\libraries\\lwjgl_util-2.9.3-linux.jar",
        "C:\\Wentra\\libraries\\lwjgl_util-2.9.4-nightly-20150209-windows.jar",
        "C:\\Wentra\\libraries\\netty-all.jar",
        "C:\\Wentra\\libraries\\oshi-core-1.1.jar",
        "C:\\Wentra\\libraries\\platform-4.5.0.jar",
        "C:\\Wentra\\libraries\\slick.jar",
        "C:\\Wentra\\libraries\\soundsystem-20120107.jar",
        "C:\\Wentra\\libraries\\trove4j-3.0.3.jar",
        "C:\\Wentra\\WentraReborn-CraftRise.jar"
    };

    std::vector<std::future<bool>> futures;
    for (const auto& jar : jar_files) {
        futures.push_back(std::async(std::launch::async, [jar]() {
            JNIEnv* local_env = nullptr;
            if (jvm->AttachCurrentThread((void**)&local_env, nullptr) != JNI_OK || !local_env) {
                std::lock_guard<std::mutex> lock(console_mutex);
                std::cerr << "[!] Thread attach başarısız: " << jar << "\n";
                return false;
            }

            bool result = JarLoader::load_jar(local_env, jar, true);

            jvm->DetachCurrentThread();
            return result;
            }));
    }

    bool all_success = true;
    for (auto& future : futures) {
        if (!future.get()) all_success = false;
    }

    std::lock_guard<std::mutex> lock(console_mutex);
    if (!all_success) {
        std::cerr << "[!] Bazı JAR dosyaları yüklenemedi.\n";
    }
    else {
        std::cout << "[*] Tüm JAR dosyaları başarıyla yüklendi.\n";
    }

    // wentra/Main sınıfını çağır
    jclass cls = env->FindClass("wentra/Main");
    if (!cls) {
        std::cerr << "[!] wentra/Main sınıfı bulunamadı!\n";
        if (env->ExceptionCheck()) {
            env->ExceptionDescribe();
            env->ExceptionClear();
        }
        return;
    }

    jmethodID mid = env->GetStaticMethodID(cls, "StartClient", "(Ljava/util/List;)V");
    if (!mid) {
        std::cerr << "[!] StartClient metodu bulunamadı!\n";
        env->DeleteLocalRef(cls);
        return;
    }

    jclass arrayListClass = env->FindClass("java/util/ArrayList");
    if (!arrayListClass) {
        std::cerr << "[!] java/util/ArrayList sınıfı bulunamadı!\n";
        env->DeleteLocalRef(cls);
        return;
    }

    jmethodID arrayListConstructor = env->GetMethodID(arrayListClass, "<init>", "()V");
    if (!arrayListConstructor) {
        std::cerr << "[!] ArrayList constructor bulunamadı!\n";
        env->DeleteLocalRef(cls);
        env->DeleteLocalRef(arrayListClass);
        return;
    }

    jobject classList = env->NewObject(arrayListClass, arrayListConstructor);
    if (!classList) {
        std::cerr << "[!] ArrayList nesnesi oluşturulamadı!\n";
        env->DeleteLocalRef(cls);
        env->DeleteLocalRef(arrayListClass);
        return;
    }

    env->CallStaticVoidMethod(cls, mid, classList);
    if (env->ExceptionOccurred()) {
        env->ExceptionDescribe();
        env->ExceptionClear();
        std::cerr << "[!] StartClient çalıştırılırken hata oluştu.\n";
    }
    else {
        std::cout << "[*] StartClient başarıyla çalıştırıldı!\n";
    }

    env->DeleteLocalRef(cls);
    env->DeleteLocalRef(arrayListClass);
    env->DeleteLocalRef(classList);
}

void __stdcall hk_glOrtho(double left, double right, double bottom, double top, double zNear, double zFar) {
    if (!env) {
        HMODULE jvmdll = Bypass::SpoofModuleLoad(XORSTR("jvm.dll"));
        if (!jvmdll) {
            std::cout << "[!] JVM modülü yüklenemedi\n";
            return;
        }

        typedef jint(JNICALL* p_GetEnv)(JavaVM* vm, JNIEnv** penv, jint version);
        p_GetEnv GetEnv = reinterpret_cast<p_GetEnv>(reinterpret_cast<DWORD>(jvmdll) + 0x144080);
        if (!GetEnv) {
            std::cout << "[!] GetEnv alınamadı\n";
            return;
        }

        if (GetEnv(jvm, &env, JNI_VERSION_1_8) != JNI_OK) {
            std::cout << "[!] JNIEnv alınamadı\n";
            return;
        }

        if (env) {
            env->GetJavaVM(&jvm);
            std::cout << "[*] JavaVM adresi: " << jvm << std::endl;

            if (jvm->GetEnv((void**)&jvmti, JVMTI_VERSION) != JNI_OK) {
                std::cout << "[!] JVMTI alınamadı\n";
                return;
            }
            std::cout << "[*] JNIEnv: " << env << ", JavaVM: " << jvm << ", JVMTI: " << jvmti << "\n";
        }
    }

    if (o_glOrtho) {
        o_glOrtho(left, right, bottom, top, zNear, zFar);
    }
    else {
        std::cout << "[!] o_glOrtho null, hook çağrılmadı\n";
    }

    static bool injected = false;
    if (!injected && env && jvm && jvmti) {
        injected = true;
        InjectAsync();
    }
}

DWORD WINAPI MainThread(LPVOID) {
    if (MH_Initialize() != MH_OK) {
        std::cout << "[!] MinHook başlatılamadı\n";
        return 1;
    }

    HMODULE hMod = Bypass::SpoofModuleLoad(XORSTR("opengl32.dll"));
    if (!hMod) {
        std::cout << "[!] OpenGL modülü yüklenemedi\n";
        return 1;
    }

    void* target = Bypass::ResolveDynamicAddress(hMod, XORSTR("glOrtho"));
    if (!target) {
        std::cout << "[!] glOrtho adresi alınamadı\n";
        return 1;
    }

    if (MH_CreateHook(target, &hk_glOrtho, reinterpret_cast<void**>(&o_glOrtho)) != MH_OK) {
        std::cout << "[!] Hook oluşturulamadı\n";
        return 1;
    }

    if (MH_EnableHook(target) != MH_OK) {
        std::cout << "[!] Hook etkinleştirilemedi\n";
        return 1;
    }

    return 0;
}

BOOL WINAPI DllMain(HMODULE hMod, DWORD reason, LPVOID) {
    if (reason == DLL_PROCESS_ATTACH) {
        AllocConsole();
        SetConsoleTitleA(XORSTR("Wentra Debug"));
        FILE* f;
        freopen_s(&f, "CONOUT$", "w", stdout);
        freopen_s(&f, "CONOUT$", "w", stderr);
        DisableThreadLibraryCalls(hMod);

        CreateThread(nullptr, 0, MainThread, nullptr, 0, nullptr);
    }
    else if (reason == DLL_PROCESS_DETACH) {
        MH_DisableHook(MH_ALL_HOOKS);
        MH_Uninitialize();
    }
    return TRUE;
}