## Hello Agora on Android Things

#### Android Things
[Android Things](https://developer.android.com/things/) lets you build smart, connected devices for a wide variety of consumer, retail, and industrial applications.


#### Architecture
![Platform Architecture](https://developer.android.com/things/images/platform-architecture.png)


#### Flash image to device

We choose [Raspberry Pi 3](https://developer.android.com/things/hardware/raspberrypi) here, you can find more hardwares/developer kits.

Download the Android Things Setup Utility from [Android Things Console](https://partner.android.com/things/console/#/tools). 

Follow instructions from https://developer.android.com/things/hardware/raspberrypi to flash the image to SD card.

Because errors happen when flashing image through the Android Things Setup Utility, so I use [etcher](https://etcher.io/) to flash the image unzipped from `quickstart-temp/image.zip`.

- `sudo etcher -d /dev/disk2 iot_rpi3.img`

After done, plugin the SD card to Raspberry Pi 3 and power on. You will get the booting success of Android Things


#### Build apps 
Build the agora app for it, it's just some minor differences from mobile device development.

Sample app from [at-iot-hello-agora](https://github.com/guohai/at-iot-hello-agora)

Clone and dig into it.


#### Reference

- https://github.com/androidthings

- https://developer.android.com/things/

- http://guoh.org/lifelog/2017/05/hello-world-android-things/

- https://developer.android.com/things/training/first-device/

- https://plus.google.com/communities/107507328426910012281

- https://android-developers.googleblog.com/2018/05/say-hello-to-android-things-10.html

- https://www.mbed.com/en/






