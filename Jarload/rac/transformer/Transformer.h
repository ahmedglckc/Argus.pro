#pragma once
#include "../Dependencies/lib/JNI/jni.h"
#include "../Dependencies/lib/JNI/jvmti.h"
#include <Windows.h>
#include <fstream>
#include <thread>
#include <iostream>


class Transformer {
public:
	static void transform(JNIEnv* env, jvmtiEnv* jvmti);

};