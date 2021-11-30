package com.mtouch.ksnet.dpt.design.appToApp.sms;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InfoBankSMS extends Thread {


//    private static Logger logger 				= LoggerFactory.getLogger( com.pgmate.pay.proc.subpg.PGWebHook.class );
//    private TrxDAO trxDAO 	= null;
    private String trxType = "";
    private HashMap<String, String> sharedMap = null;
    private final String SMS_URL = "https://sms.supersms.co:7020/sms/v3/multiple-destinations";

//    static {
//        disableSslVerification();
//    }


    public InfoBankSMS(HashMap<String, String> sharedMap) {
        this.sharedMap 	= sharedMap;
    }


    public void run(){
//        SharedMap<String,Object> ntsMap = new SharedMap<String,Object>();

        if(!PatternUtil.isCellphoneNo(sharedMap.get("payerTel"))) {
            return;
        }

        HashMap<String, Object> jsonMap = new HashMap<String, Object>();
        List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> map2 = new HashMap<String, Object>();

        map2.put("to", "82"+sharedMap.get("payerTel").substring(1).replaceAll("-", ""));
        list.add(map2);
        jsonMap.put("destinations", list);
        jsonMap.put("from","18551838");
        jsonMap.put("ttl","0");

        StringBuffer sb = new StringBuffer();

        sb.append("가맹점:");
        sb.append(sharedMap.get("name"));
        sb.append("\n");
        sb.append("카드사:");
        sb.append(sharedMap.get("issure"));
        sb.append("\n");
        sb.append("카드번호:");
        sb.append(sharedMap.get("cardNumber"));
        sb.append("\n");
        sb.append("승인결과:");
        sb.append(sharedMap.get("trxResult"));
        sb.append("\n");
        sb.append("승인번호:");
        sb.append(sharedMap.get("authCd"));
        sb.append("\n");
        sb.append("승인금액:");
        sb.append(sharedMap.get("amount"));
        sb.append("\n");
        sb.append("할부기간:");
        sb.append(sharedMap.get("installment"));
        sb.append("\n");
        sb.append("승인일자:");
        sb.append(sharedMap.get("regDate"));



        jsonMap.put("text",sb.toString());
        String json = "";//jsonMap.toJson();

        System.out.println(json);

        try {
            URL url = new URL(SMS_URL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setConnectTimeout(10000);
            con.setReadTimeout(10000);

            String authKey = "";//trxDAO.getInfoBankSmsKey();

            con.addRequestProperty("Accept", "application/json");
            con.addRequestProperty("Authorization", authKey);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");

            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setDefaultUseCaches(false);

            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
            wr.write(json);
            wr.flush();

            String resData = "";
            StringBuilder sb2 = new StringBuilder();


            if(con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "utf-8"));
                String line;
                while ((line = br.readLine()) != null) {
                    sb2.append(line).append("\n");
                }
                br.close();

                resData = sb2.toString();
//                logger.("resData [{}]",resData);


            }



        }catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }


}
