
#include "Transformer.h"
#include <vector>

struct ClassTransformer {
    jclass class_name;
    const char* transformer_class_name;
};

std::vector<ClassTransformer> transformers;

const char* getClassName(JNIEnv* env, jclass cls) {
    jclass classClass = env->GetObjectClass(cls);
    jmethodID getName = env->GetMethodID(classClass, "getName", "()Ljava/lang/String;");
    jstring name = (jstring)env->CallObjectMethod(cls, getName);
    const char* nameChars = env->GetStringUTFChars(name, nullptr);
    env->DeleteLocalRef(classClass);
    env->DeleteLocalRef(name);
    return nameChars;
}


static void JNICALL
ClassFileLoadHook_callback(jvmtiEnv* jvmti_env,
    JNIEnv* jni_env,
    jclass class_being_redefined,
    jobject loader,
    const char* name,
    jobject protection_domain,
    jint class_data_len,
    const unsigned char* class_data,
    jint* new_class_data_len,
    unsigned char** new_class_data)
{
    if (!class_being_redefined) {
       // std::cout << "Error: " << (name ? name : "unknown") << std::endl;
        return;
    }

    std::cout << "Retransformed " << (name ? name : "unknown") << std::endl;

    for (const auto& transformer : transformers) {
        const char* redefinedClassName = getClassName(jni_env, class_being_redefined);
        const char* transformerClassName = getClassName(jni_env, transformer.class_name);

        if (redefinedClassName && transformerClassName) {


            if (strcmp(redefinedClassName, transformerClassName) == 0) {


                jbyteArray originalBytes = jni_env->NewByteArray(class_data_len);
                if (originalBytes == nullptr) {
                    std::cout << "byte not found" << std::endl;
                    break;
                }
                std::cout << "Transformed Classes : " << transformer.transformer_class_name << std::endl;
                jni_env->SetByteArrayRegion(originalBytes, 0, class_data_len, (jbyte*)class_data);

                jclass transformerClass = jni_env->FindClass(transformer.transformer_class_name);
                if (transformerClass == nullptr) {
                    std::cout << "Failed Transformer " << transformer.transformer_class_name << std::endl;
                    break;
                }

                jmethodID transformMethod = jni_env->GetStaticMethodID(transformerClass, "transform", "([B)[B");
                if (transformMethod == nullptr) {
                    std::cout << "Transformer Method not found" << transformer.transformer_class_name << std::endl;
                    break;
                }

                jbyteArray ClassBytesNew = (jbyteArray)jni_env->CallStaticObjectMethod(transformerClass, transformMethod, originalBytes);
                if (ClassBytesNew == nullptr) {
                    std::cout << "bytes: " << ClassBytesNew << std::endl;

                    break;
                }

                jsize newClassSize = jni_env->GetArrayLength(ClassBytesNew);
                if (newClassSize <= 0) {

                    break;
                }


                unsigned char* newClassByteBuff = nullptr;
                jvmtiError err = jvmti_env->Allocate(newClassSize, &newClassByteBuff);
                if (err != JVMTI_ERROR_NONE) {

                    break;
                }

                jni_env->GetByteArrayRegion(ClassBytesNew, 0, newClassSize, (jbyte*)newClassByteBuff);


                *new_class_data_len = newClassSize;
                *new_class_data = newClassByteBuff;

                std::cout << "Success Transformer " << transformerClassName << std::endl;
                break;
            }
            else {

            }
        }
        else {

        }
    }
}


void Transformer::transform(JNIEnv* env, jvmtiEnv* jvmti) {
    std::cout << "TransformerStarted" << std::endl;
    jvmtiCapabilities cap{};
    cap.can_retransform_classes = JVMTI_ENABLE;
    cap.can_retransform_any_class = JVMTI_ENABLE;
    if (jvmti->AddCapabilities(&cap) != JVMTI_ERROR_NONE) {
        std::cerr << "Retransform classes not supported\n";
    }

    jvmtiEventCallbacks callbacks{};
    callbacks.ClassFileLoadHook = ClassFileLoadHook_callback;
    if (jvmti->SetEventCallbacks(&callbacks, sizeof(jvmtiEventCallbacks)) != JVMTI_ERROR_NONE) {
        std::cerr << "Failed to set event callback\n";
    }
    jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_CLASS_FILE_LOAD_HOOK, nullptr);

    std::string JavaClass = "wentra/utils/mapper/transformers/";
    std::string mName = "getTargetClass";

    std::string C02Packet = JavaClass + "C02PacketTransformer";
    jclass C02PacketFindClass = env->FindClass(C02Packet.c_str());
    jmethodID C02PacketGetStaticMethodID = env->GetStaticMethodID(C02PacketFindClass, mName.c_str(), "()Ljava/lang/Class;");
    jclass C02PacketCallStaticObjectMethod = (jclass)env->CallStaticObjectMethod(C02PacketFindClass, C02PacketGetStaticMethodID);
    ClassTransformer C02PacketTransformer = { C02PacketCallStaticObjectMethod, C02Packet.c_str() };

    std::string GuiIngame = JavaClass + "GuiIngameTransformer";
    jclass GuiIngameFindClass = env->FindClass(GuiIngame.c_str());
    jmethodID GuiIngameGetStaticMethodID = env->GetStaticMethodID(GuiIngameFindClass, mName.c_str(), "()Ljava/lang/Class;");
    jclass GuiIngameCallStaticObjectMethod = (jclass)env->CallStaticObjectMethod(GuiIngameFindClass, GuiIngameGetStaticMethodID);
    ClassTransformer GuiIngameTransformer = { GuiIngameCallStaticObjectMethod, GuiIngame.c_str() };

    std::string FontRenderer = JavaClass + "FontRendererTransformer";
    jclass FontRendererFindClass = env->FindClass(FontRenderer.c_str());
    jmethodID FontRendererGetStaticMethodID = env->GetStaticMethodID(FontRendererFindClass, mName.c_str(), "()Ljava/lang/Class;");
    jclass FontRendererCallStaticObjectMethod = (jclass)env->CallStaticObjectMethod(FontRendererFindClass, FontRendererGetStaticMethodID);
    ClassTransformer FontRendererTransformer = { FontRendererCallStaticObjectMethod, FontRenderer.c_str() };

    std::string Network = JavaClass + "NetworkTransformer";
    jclass NetworkFindClass = env->FindClass(Network.c_str());
    jmethodID NetworkGetStaticMethodID = env->GetStaticMethodID(NetworkFindClass, mName.c_str(), "()Ljava/lang/Class;");
    jclass NetworkCallStaticObjectMethod = (jclass)env->CallStaticObjectMethod(NetworkFindClass, NetworkGetStaticMethodID);
    ClassTransformer NetworkTransformer = { NetworkCallStaticObjectMethod, Network.c_str() };

    std::string S12Packet = JavaClass + "S12PacketTransformer";
    jclass S12PacketFindClass = env->FindClass(S12Packet.c_str());
    jmethodID S12PacketGetStaticMethodID = env->GetStaticMethodID(S12PacketFindClass, mName.c_str(), "()Ljava/lang/Class;");
    jclass S12PacketCallStaticObjectMethod = (jclass)env->CallStaticObjectMethod(S12PacketFindClass, S12PacketGetStaticMethodID);
    ClassTransformer S12PacketTransformer = { S12PacketCallStaticObjectMethod, S12Packet.c_str() };

    std::string RenderManager = JavaClass + "RenderManagerTransformer";
    jclass RenderManagerFindClass = env->FindClass(RenderManager.c_str());
    jmethodID RenderManagerGetStaticMethodID = env->GetStaticMethodID(RenderManagerFindClass, mName.c_str(), "()Ljava/lang/Class;");
    jclass RenderManagerCallStaticObjectMethod = (jclass)env->CallStaticObjectMethod(RenderManagerFindClass, RenderManagerGetStaticMethodID);
    ClassTransformer RenderManagerTransformer = { RenderManagerCallStaticObjectMethod, RenderManager.c_str() };

    std::string EffectRenderer = JavaClass + "EffectRendererTransformer";
    jclass EffectRendererFindClass = env->FindClass(EffectRenderer.c_str());
    jmethodID EffectRendererGetStaticMethodID = env->GetStaticMethodID(EffectRendererFindClass, mName.c_str(), "()Ljava/lang/Class;");
    jclass EffectRendererCallStaticObjectMethod = (jclass)env->CallStaticObjectMethod(EffectRendererFindClass, EffectRendererGetStaticMethodID);
    ClassTransformer EffectRendererTransformer = { EffectRendererCallStaticObjectMethod, EffectRenderer.c_str() };

    std::string Entity = JavaClass + "EntityTransformer";
    jclass EntityFindClass = env->FindClass(Entity.c_str());
    jmethodID EntityGetStaticMethodID = env->GetStaticMethodID(EntityFindClass, mName.c_str(), "()Ljava/lang/Class;");
    jclass EntityCallStaticObjectMethod = (jclass)env->CallStaticObjectMethod(EntityFindClass, EntityGetStaticMethodID);
    ClassTransformer EntityTransformer = { EntityCallStaticObjectMethod, Entity.c_str() };

    std::string EntityLivingBase = JavaClass + "EntityLivingBaseTransformer";
    jclass EntityLivingBaseFindClass = env->FindClass(EntityLivingBase.c_str());
    jmethodID EntityLivingBaseGetStaticMethodID = env->GetStaticMethodID(EntityLivingBaseFindClass, mName.c_str(), "()Ljava/lang/Class;");
    jclass EntityLivingBaseCallStaticObjectMethod = (jclass)env->CallStaticObjectMethod(EntityLivingBaseFindClass, EntityLivingBaseGetStaticMethodID);
    ClassTransformer EntityLivingBaseTransformer = { EntityLivingBaseCallStaticObjectMethod, EntityLivingBase.c_str() };

    std::string FloatContainer = JavaClass + "FloatContainerTransformer";
    jclass FloatContainerFindClass = env->FindClass(FloatContainer.c_str());
    jmethodID FloatContainerGetStaticMethodID = env->GetStaticMethodID(FloatContainerFindClass, mName.c_str(), "()Ljava/lang/Class;");
    jclass FloatContainerCallStaticObjectMethod = (jclass)env->CallStaticObjectMethod(FloatContainerFindClass, FloatContainerGetStaticMethodID);
    ClassTransformer FloatContainerTransformer = { FloatContainerCallStaticObjectMethod, FloatContainer.c_str() };


    std::string MotionContainer = JavaClass + "MotionContainerTransformer";
    jclass MotionContainerFindClass = env->FindClass(MotionContainer.c_str());
    jmethodID MotionContainerGetStaticMethodID = env->GetStaticMethodID(MotionContainerFindClass, mName.c_str(), "()Ljava/lang/Class;");
    jclass MotionContainerCallStaticObjectMethod = (jclass)env->CallStaticObjectMethod(MotionContainerFindClass, MotionContainerGetStaticMethodID);
    ClassTransformer MotionContainerTransformer = { MotionContainerCallStaticObjectMethod, MotionContainer.c_str() };

    std::string S18PacketEntityTeleport = JavaClass + "S18PacketEntityTeleportTransformer";
    jclass S18PacketEntityTeleportFindClass = env->FindClass(S18PacketEntityTeleport.c_str());
    jmethodID S18PacketEntityTeleportGetStaticMethodID = env->GetStaticMethodID(S18PacketEntityTeleportFindClass, mName.c_str(), "()Ljava/lang/Class;");
    jclass S18PacketEntityTeleportCallStaticObjectMethod = (jclass)env->CallStaticObjectMethod(S18PacketEntityTeleportFindClass, S18PacketEntityTeleportGetStaticMethodID);
    ClassTransformer S18PacketEntityTeleportTransformer = { S18PacketEntityTeleportCallStaticObjectMethod, S18PacketEntityTeleport.c_str() };

    std::string NetworkPlayerInfo = JavaClass + "NetworkPlayerInfoTransformer";
    jclass NetworkPlayerInfoFindClass = env->FindClass(NetworkPlayerInfo.c_str());
    jmethodID NetworkPlayerInfoGetStaticMethodID = env->GetStaticMethodID(NetworkPlayerInfoFindClass, mName.c_str(), "()Ljava/lang/Class;");
    jclass NetworkPlayerInfoCallStaticObjectMethod = (jclass)env->CallStaticObjectMethod(NetworkPlayerInfoFindClass, NetworkPlayerInfoGetStaticMethodID);
    ClassTransformer NetworkPlayerInfoTransformer = { NetworkPlayerInfoCallStaticObjectMethod, NetworkPlayerInfo.c_str() };

    std::string AbstractClientPlayer = JavaClass + "AbstractClientPlayerTransformer";
    jclass AbstractClientPlayerFindClass = env->FindClass(AbstractClientPlayer.c_str());
    jmethodID AbstractClientPlayerGetStaticMethodID = env->GetStaticMethodID(AbstractClientPlayerFindClass, mName.c_str(), "()Ljava/lang/Class;");
    jclass AbstractClientPlayerCallStaticObjectMethod = (jclass)env->CallStaticObjectMethod(AbstractClientPlayerFindClass, AbstractClientPlayerGetStaticMethodID);
    ClassTransformer AbstractClientPlayerTransformer = { AbstractClientPlayerCallStaticObjectMethod, AbstractClientPlayer.c_str() };

    std::string KeyBinding = JavaClass + "KeyBindingTransformer";
    jclass KeyBindingFindClass = env->FindClass(KeyBinding.c_str());
    jmethodID KeyBindingGetStaticMethodID = env->GetStaticMethodID(KeyBindingFindClass, mName.c_str(), "()Ljava/lang/Class;");
    jclass KeyBindingCallStaticObjectMethod = (jclass)env->CallStaticObjectMethod(KeyBindingFindClass, KeyBindingGetStaticMethodID);
    ClassTransformer KeyBindingTransformer = { KeyBindingCallStaticObjectMethod, KeyBinding.c_str() };

    std::string GameSettings = JavaClass + "GameSettingsTransformer";
    jclass GameSettingsFindClass = env->FindClass(GameSettings.c_str());
    jmethodID GameSettingsGetStaticMethodID = env->GetStaticMethodID(GameSettingsFindClass, mName.c_str(), "()Ljava/lang/Class;");
    jclass GameSettingsCallStaticObjectMethod = (jclass)env->CallStaticObjectMethod(GameSettingsFindClass, GameSettingsGetStaticMethodID);
    ClassTransformer GameSettingsTransformer = { GameSettingsCallStaticObjectMethod, GameSettings.c_str() };

    transformers.push_back(C02PacketTransformer);
    transformers.push_back(GuiIngameTransformer);
    transformers.push_back(FontRendererTransformer);
    transformers.push_back(NetworkTransformer);
    transformers.push_back(S12PacketTransformer);
    transformers.push_back(RenderManagerTransformer);
    transformers.push_back(EffectRendererTransformer);
    transformers.push_back(EntityTransformer);
    transformers.push_back(EntityLivingBaseTransformer);
    transformers.push_back(FloatContainerTransformer);
    transformers.push_back(MotionContainerTransformer);
    transformers.push_back(S18PacketEntityTeleportTransformer);
    transformers.push_back(NetworkPlayerInfoTransformer);
    transformers.push_back(AbstractClientPlayerTransformer);
    transformers.push_back(KeyBindingTransformer);
    transformers.push_back(GameSettingsTransformer);

    jclass classes_to_rt[] = {
        C02PacketCallStaticObjectMethod,
        GuiIngameCallStaticObjectMethod,
        FontRendererCallStaticObjectMethod,
        NetworkCallStaticObjectMethod,
        S12PacketCallStaticObjectMethod,
        RenderManagerCallStaticObjectMethod,
        EffectRendererCallStaticObjectMethod,
        EntityCallStaticObjectMethod,
        EntityLivingBaseCallStaticObjectMethod,
        FloatContainerCallStaticObjectMethod,
        MotionContainerCallStaticObjectMethod,
        S18PacketEntityTeleportCallStaticObjectMethod,
        NetworkPlayerInfoCallStaticObjectMethod,
        AbstractClientPlayerCallStaticObjectMethod,
        KeyBindingCallStaticObjectMethod,
        GameSettingsCallStaticObjectMethod
    };


    jvmtiError error = jvmti->RetransformClasses(sizeof(classes_to_rt) / sizeof(jclass), classes_to_rt);
}