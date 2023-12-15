
#include <jni.h>
#include "com_example_myapplication_Process.h"

#include <opencv2/opencv.hpp>

using namespace cv;

extern "C"{

JNIEXPORT void JNICALL
// 프로젝트 명 변경 com-example-myapplication
Java_com_example_myapplication_Process_ConvertRGBtoGray(
        JNIEnv *env,
jclass instance,
        jlong matAddrInput,
jlong matAddrResult){


Mat &matInput = *(Mat *)matAddrInput;
Mat &matResult = *(Mat *)matAddrResult;

cvtColor(matInput, matResult, COLOR_RGBA2GRAY);


}
}