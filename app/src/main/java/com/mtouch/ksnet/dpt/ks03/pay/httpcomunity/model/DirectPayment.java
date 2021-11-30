package com.mtouch.ksnet.dpt.ks03.pay.httpcomunity.model;

import com.pswseoul.util.GsonUtil;

import java.util.HashMap;

public class DirectPayment {
    /**
      * {
     * 	"pay": {
     * 		"trxType": "ONTR",
     * 		"tmnId": "test0005",
     * 		"trackId": "AXD_1567555832213",
     * 		"amount": "1004",
     * 		"payerTel": "01092602041",
     * 		"udf1": "",
     * 		"udf2": "",
     * 		"products": [{
     * 			"name": "테스트"
                        *                }],
     * 		"card": {
     * 			"number": "5409260510210012",
     * 			"expiry": "2301",
     * 			"installment": "0"
                        *        },
     * 		"metadata": {
     * 			"cardAuth": "false",
     * 			"authPw": "",
     * 			"authDob": ""
                        *        }
     *    }
     * }
    **/

    public HashMap<String, Object> pay = new HashMap<>();



    public String toJsonString(){
        return  GsonUtil.toJson(this,true,"");
    }
}
