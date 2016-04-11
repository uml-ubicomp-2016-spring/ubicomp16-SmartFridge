#include <jni.h>

JNIEXPORT jlong JNICALL
Java_ubicomp_cs_uml_edu_smartfridge_DetectionBasedTracker_nativeCreateObject(JNIEnv *env,
                                                                             jclass type,
                                                                             jstring cascadeName_,
                                                                             jint minFaceSize) {

    // TODO
;
}

JNIEXPORT void JNICALL
Java_ubicomp_cs_uml_edu_smartfridge_DetectionBasedTracker_nativeDestroyObject(JNIEnv *env,
                                                                              jclass type,
                                                                              jlong thiz) {

    // TODO

}

JNIEXPORT void JNICALL
Java_ubicomp_cs_uml_edu_smartfridge_DetectionBasedTracker_nativeStart(JNIEnv *env, jclass type,
                                                                      jlong thiz) {

    // TODO

}

JNIEXPORT void JNICALL
Java_ubicomp_cs_uml_edu_smartfridge_DetectionBasedTracker_nativeStop(JNIEnv *env, jclass type,
                                                                     jlong thiz) {

    // TODO

}

JNIEXPORT void JNICALL
Java_ubicomp_cs_uml_edu_smartfridge_DetectionBasedTracker_nativeSetFaceSize(JNIEnv *env,
                                                                            jclass type, jlong thiz,
                                                                            jint size) {

    // TODO

}


}

JNIEXPORT void JNICALL
Java_ubicomp_cs_uml_edu_smartfridge_DetectionBasedTracker_nativeDetect(JNIEnv *env, jclass type,
                                                                       jlong thiz, jlong inputImage,
                                                                       jlong faces) {

    // TODO

}
