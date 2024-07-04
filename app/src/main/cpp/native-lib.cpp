//
// Created by ermoh on 27-04-2023.
//
#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_qureka_skool_CredentialHelper_singularAppKey(JNIEnv *env, jclass clazz) {
    std::string baseURL = "";
    return env->NewStringUTF(baseURL.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_qureka_skool_CredentialHelper_globalConfigAppKey(JNIEnv *env, jclass clazz) {
    std::string baseURL = "05746231975%";
    return env->NewStringUTF(baseURL.c_str());
}
