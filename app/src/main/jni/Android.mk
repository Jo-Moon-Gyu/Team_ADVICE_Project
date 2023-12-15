LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

#opencv library
# 파일 경로로 변경C:\Users\smhrd\AndroidProject\MyApplication

OPENCVROOT:= C:\Users\Moon_94\AndroidStudioProjects\ADVISER\OpenCV
OPENCV_CAMERA_MODULES:=on
OPENCV_INSTALL_MODULES:=on
OPENCV_LIB_TYPE:=SHARED
include ${OPENCVROOT}\native\jni\OpenCV.mk


LOCAL_MODULE    := native-lib
LOCAL_SRC_FILES := main.cpp
LOCAL_LDLIBS += -llog

include $(BUILD_SHARED_LIBRARY)