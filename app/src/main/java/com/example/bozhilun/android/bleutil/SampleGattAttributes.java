/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.bozhilun.android.bleutil;

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 * wyl
 */
public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    public static String HEART_RATE_SERVER = "F0080001-0451-4000-B000-000000000000";
    public static String CLIENT_CHARACTERISTIC_DATA = "F0080002-0451-4000-B000-000000000000";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "F0080003-0451-4000-B000-000000000000";

    private static HashMap<String, String> attributp = new HashMap();
    public static String HEART_RATE_SERVERp = "6E400001-B5A3-F393-E0A9-E50E24DCCA9E";//服务
    public static String CLIENT_CHARACTERISTIC_DATAp = "6E400003-B5A3-F393-E0A9-E50E24DCCA9E";//读
    public static String CLIENT_CHARACTERISTIC_CONFIGp = "6E400002-B5A3-F393-E0A9-E50E24DCCA9E";//写

    public static String HEART_RATE_MEASUREMENT = "0000ffe1-0000-1000-8000-00805f9b34fb";

    public static String DESC = "00002902-0000-1000-8000-00805f9b34fb";

    static {
        // Sample Services.
        attributp.put(HEART_RATE_SERVERp, "Servicep");
        attributp.put(CLIENT_CHARACTERISTIC_DATAp, "DATAp");
        attributp.put(CLIENT_CHARACTERISTIC_CONFIGp, "LANYA CONFIGp");
    }

    static {
        // Sample Services.
        attributes.put(HEART_RATE_SERVER, "Service");
        attributes.put(CLIENT_CHARACTERISTIC_DATA, "DATA");
        attributes.put(CLIENT_CHARACTERISTIC_CONFIG, "LANYA CONFIG");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }


    //sis watch uuids
    public static final String BZLUN_IKP_SERVER_UUID = "0000a800-0000-1000-8000-00805f9b34fb"; //server uuid
    public static final String BZLUN_IKP_READ_UUID = "0000a801-0000-1000-8000-00805f9b34fb";    //read uuid
    public static final String BZLUN_IKP_WRITE_UUID = "0000a802-0000-1000-8000-00805f9b34fb";   //write uuid


}