#include "includes.h"
#include <iostream>
#include <thread>
#include <chrono>



JavaVM* jvm = nullptr;
JNIEnv* env = nullptr;
inline jvmtiEnv* jvmti;

namespace OpenGL {
    typedef void(__stdcall* p_glOrtho)(double, double, double, double, double, double);
    p_glOrtho glOrtho = nullptr;
}
void UnhookRestoreMethod(LPVOID address, PBYTE bytes) {
    DWORD oldProtect;
    VirtualProtect(address, sizeof(bytes), PAGE_EXECUTE_READWRITE, &oldProtect);
    memcpy(address, bytes, sizeof(bytes));
    VirtualProtect(address, sizeof(bytes), oldProtect, &oldProtect);
}

BYTE o_GetStaticObj[] = { 0x48,0x89,0x5C,0x24,0x08,0x48,0x89,0x6C };
BYTE o_GetFieldID[] = { 0x48,0x89,0x5C,0x24,0x10,0x48,0x89,0x6C };
BYTE o_GetMethodID[] = { 0x48,0x89,0x5C,0x24,0x08,0x48,0x89,0x6C };
BYTE o_GetObjField[] = { 0x48,0x8B,0xC4,0x53,0x56,0x48,0x83,0xEC };
BYTE o_FindClass[] = { 0x48,0x89,0x5C,0x24,0x18,0x48,0x89,0x6C };
BYTE o_GetJVMs[] = { 0x8B,0x05,0xC6,0xEA,0x6B,0x00,0x85,0xC0 };
BYTE o_AttachThread[] = { 0x8B,0x05,0x26,0xD5,0x69,0x00,0x4D,0x8B };
BYTE o_AddSys[] = { 0x48,0x89,0x74,0x24,0x10,0x57,0x48,0x83 };

BYTE b_GetClasses[] = { 0x48,0x89,0x6C,0x24,0x10,0x48,0x89,0x74 };

BYTE p_AddCap[] = { 0x48,0x89,0x74,0x24,0x10,0x57,0x48,0x83 };
BYTE p_Retrans[] = { 0x48,0x89,0x6C,0x24,0x10,0x48,0x89,0x74 };
BYTE p_SetCB[] = { 0x48,0x89,0x6C,0x24,0x10,0x48,0x89,0x74 };
BYTE p_SetNotif[] = { 0x40,0x55,0x56,0x57,0x41,0x54,0x48,0x83 };

void debug(const char* msg...) {
    std::cout << msg << std::endl;
}

typedef jvmtiError(__stdcall* p_hook)(jvmtiEnv* env, jint* class_count_ptr, jclass** classes_ptr);
p_hook o_GetLoadedClasses = nullptr;




bool sikis = false;
typedef jvmtiError(JNICALL* jvmtiGetLoadedClasses)(jvmtiEnv* env, jint* class_count_ptr, jclass** classes_ptr);
jvmtiGetLoadedClasses reallyGetLoadedClasses = nullptr;
jvmtiError JNICALL HookedGetLoadedClasses(jvmtiEnv* env, jint* class_count_ptr, jclass** classes_ptr) {
    jclass* classes = nullptr;
    jint class_count = 0;
    jvmtiError return_value = reallyGetLoadedClasses(env, &class_count, &classes);
    *class_count_ptr = JVMTI_ERROR_NULL_POINTER + 30;
    *classes_ptr = classes;
    return return_value;
}


void CrystalMain(HINSTANCE instance) {
    UnhookRestoreMethod(JNI_GetCreatedJavaVMs, o_GetJVMs);


    jint result = JNI_GetCreatedJavaVMs(&jvm, 1, nullptr);
    UnhookRestoreMethod(jvm->functions->AttachCurrentThread, o_AttachThread);

    if (result == JNI_OK && jvm) {
        jint attachResult = jvm->AttachCurrentThread((void**)&env, nullptr);
        if (attachResult == JNI_OK && env) {
            jvm->GetEnv((void**)&jvmti, JVMTI_VERSION_1_2);
            UnhookRestoreMethod(jvmti->functions->AddToSystemClassLoaderSearch, o_AddSys);
            std::cout << "JVM -> " << jvm << std::endl;
            std::cout << "JNIENV -> " << env << std::endl;
            std::cout << "JVMTIENV -> " << jvmti << std::endl;

            UnhookRestoreMethod(jvmti->functions->GetLoadedClasses, b_GetClasses);

            UnhookRestoreMethod(jvmti->functions->AddCapabilities, p_AddCap);
            UnhookRestoreMethod(jvmti->functions->RetransformClasses, p_Retrans);
            UnhookRestoreMethod(jvmti->functions->SetEventCallbacks, p_SetCB);
            UnhookRestoreMethod(jvmti->functions->SetEventNotificationMode, p_SetNotif);
            MH_Initialize();
            uintptr_t targetAddress = (uintptr_t)jvmti->functions->GetLoadedClasses;
            MH_CreateHook(reinterpret_cast<void*>(targetAddress), &HookedGetLoadedClasses, reinterpret_cast<void**>(&reallyGetLoadedClasses));
            MH_EnableHook(reinterpret_cast<void*>(targetAddress));
            std::this_thread::sleep_for(std::chrono::milliseconds(1700));
            jvmti->AddToSystemClassLoaderSearch("C:\\Wentra\\WentraReborn-CraftRise.jar");

            std::this_thread::sleep_for(std::chrono::milliseconds(500));
            jclass Metalix = env->FindClass("wentra/Main");
            jmethodID Start = env->GetStaticMethodID(Metalix, "StartClient", "(Ljava/util/List;)V");
            jint class_count = 0;
            jclass* classes = nullptr;
            jvmtiError result = reallyGetLoadedClasses(jvmti, &class_count, &classes);
            if (result != JVMTI_ERROR_NONE) {
                return;
            }
            jclass arrayListClass = env->FindClass("java/util/ArrayList");
            jmethodID arrayListConstructor = env->GetMethodID(arrayListClass, "<init>", "()V");
            jobject classList = env->NewObject(arrayListClass, arrayListConstructor);
            for (int i = 0; i < class_count; ++i) {
                jmethodID addMethod = env->GetMethodID(arrayListClass, "add", "(Ljava/lang/Object;)Z");
                env->CallBooleanMethod(classList, addMethod, classes[i]);
            }
            env->CallStaticVoidMethod(Metalix, Start, classList);
            std::this_thread::sleep_for(std::chrono::milliseconds(2500));
            Transformer::transform(env, jvmti);
        }
    }
}
BOOL WINAPI DllMain(HMODULE hMod, DWORD dwReason, LPVOID lpReserved)
{
    switch (dwReason) {
    case DLL_PROCESS_ATTACH: {
        AllocConsole();
        freopen("CONOUT$", "w", stdout);
        DisableThreadLibraryCalls(hMod);

        std::thread(CrystalMain, hMod).detach();


    } break;
    }
    return TRUE;
}

