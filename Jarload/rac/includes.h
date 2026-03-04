#pragma comment(lib, "jvm.lib")
#pragma comment(lib, "libMinHook-x64.lib")
#pragma comment(lib, "ws2_32.lib")
#pragma comment(lib, "ntdll.lib")
#pragma comment(lib, "Dbghelp.lib")
#define _CRT_SECURE_NO_WARNINGS
#define WriteLine(x) (std::cout << x << std::endl)

#include <ws2tcpip.h>
#include <Windows.h>
#include <thread>
#include <chrono>
#include <iostream>
#include <sstream>
#include <cmath>
#include <filesystem>
#include <cstdlib>
#include <fstream>
#include <ctime>
#include <string>
#include <intrin.h>
#include "Dependencies/lib/JNI/jni.h"
#include "Dependencies/lib/JNI/jvmti.h"
#include "psapi.h"
#include <random>
#include <mutex>
#include "XorStr.hpp"
#include "Dependencies/include/MinHook.h"
#include "transformer/Transformer.h"
