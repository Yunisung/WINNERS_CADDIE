package com.mtouch.ksnet.dpt.db;

import org.json.JSONObject;

import io.realm.RealmObject;

public class PaymentInfo extends RealmObject {

    public boolean isRegisted;

    public String token;

    public String tmnId;
    public String serial;
    public String appId;
    public String mchtId;
    public String version;
    public String telNo;

    public String mchtName;
    public String mchtAddr;
    public String mchtBizNum;

    public String trackId;
    public String cardCashSe;
    public String delngSe;
    public String setleSuccesAt;
    public String setleMssage;
    public String confmNo;
    public String regDate;
    public String cardNo;
    public String instlmtMonth;
    public String issuCmpnyCode;
    public String issuCmpnyNm;
    public String puchasCmpnyCode;
    public String puchasCmpnyNm;
    public String aditInfo;
    public String splpc;
    public String vat;
    public String taxxpt;
    public String svcpc;
    public String delngNo;
    public String trxId;

    public String toListItemString() {
        return "isRegisted= " + isRegisted +
                "\ntoken= " + token +
                "\ntmnId= " + tmnId +
//                "\nserial= " + serial +
//                "\nappId= " + appId +
//                "\nmchtId= " + mchtId +
                "\nversion= " + version +
//                "\ntelNo= " + telNo +
                "\nmchtName= " + mchtName +
                "\ntrackId= " + trackId +
                "\ncardCashSe= " + cardCashSe +
                "\ndelngSe= " + delngSe +
                "\nsetleSuccesAt= " + setleSuccesAt +
                "\nsetleMssage= " + setleMssage +
                "\nconfmNo= " + confmNo +
                "\nregDate= " + regDate +
                "\ncardNo= " + cardNo +
                "\ninstlmtMonth= " + instlmtMonth +
                "\nissuCmpnyCode= " + issuCmpnyCode +
                "\nissuCmpnyNm= " + issuCmpnyNm +
                "\npuchasCmpnyCode= " + puchasCmpnyCode +
                "\npuchasCmpnyNm= " + puchasCmpnyNm +
                "\nsplpc= " + splpc +
                "\nvat= " + vat +
                "\ntaxxpt= " + taxxpt +
                "\nsvcpc= " + svcpc +
                "\ndelngNo= " + delngNo +
                "\ntrxId= " + trxId +
                "\naditInfo= " + aditInfo;
    }

    @Override
    public String toString() {
        return "PaymentInfo{" +
                "isRegisted=" + isRegisted +
                ", token='" + token + '\'' +
                ", tmnId='" + tmnId + '\'' +
                ", serial='" + serial + '\'' +
                ", appId='" + appId + '\'' +
                ", mchtId='" + mchtId + '\'' +
                ", version='" + version + '\'' +
                ", telNo='" + telNo + '\'' +
                ", mchtName='" + mchtName + '\'' +
                ", trackId='" + trackId + '\'' +
                ", cardCashSe='" + cardCashSe + '\'' +
                ", delngSe='" + delngSe + '\'' +
                ", setleSuccesAt='" + setleSuccesAt + '\'' +
                ", setleMssage='" + setleMssage + '\'' +
                ", confmNo='" + confmNo + '\'' +
                ", regDate='" + regDate + '\'' +
                ", cardNo='" + cardNo + '\'' +
                ", instlmtMonth='" + instlmtMonth + '\'' +
                ", issuCmpnyCode='" + issuCmpnyCode + '\'' +
                ", issuCmpnyNm='" + issuCmpnyNm + '\'' +
                ", puchasCmpnyCode='" + puchasCmpnyCode + '\'' +
                ", puchasCmpnyNm='" + puchasCmpnyNm + '\'' +
                ", aditInfo='" + aditInfo + '\'' +
                ", splpc='" + splpc + '\'' +
                ", vat='" + vat + '\'' +
                ", taxxpt='" + taxxpt + '\'' +
                ", svcpc='" + svcpc + '\'' +
                ", delngNo='" + delngNo + '\'' +
                ", trxId='" + trxId + '\'' +
                '}';
    }

    public String toJSONString() {
        return "{" +
                "\"isRegisted\":\"" + isRegisted + "\"" +
                "\"token\":\"" + token + "\"" +
                "\"tmnId\":\"" + tmnId + "\"" +
                "\"serial\":\"" + serial + "\"" +
                "\"appId\":\"" + appId + "\"" +
                "\"mchtId\":\"" + mchtId + "\"" +
                "\"version\":\"" + version + "\"" +
                "\"telNo\":\"" + telNo + "\"" +
                "\"mchtName\":\"" + mchtName + "\"" +
                "\"trackId\":\"" + trackId + "\"" +
                "\"cardCashSe\":\"" + cardCashSe + "\"" +
                "\"delngSe\":\"" + delngSe + "\"" +
                "\"setleSuccesAt\":\"" + setleSuccesAt + "\"" +
                "\"setleMssage\":\"" + setleMssage + "\"" +
                "\"confmNo\":\"" + confmNo + "\"" +
                "\"regDate\":\"" + regDate + "\"" +
                "\"cardNo\":\"" + cardNo + "\"" +
                "\"instlmtMonth\":\"" + instlmtMonth + "\"" +
                "\"issuCmpnyCode\":\"" + issuCmpnyCode + "\"" +
                "\"issuCmpnyNm\":\"" + issuCmpnyNm + "\"" +
                "\"puchasCmpnyCode\":\"" + puchasCmpnyCode + "\"" +
                "\"puchasCmpnyNm\":\"" + puchasCmpnyNm + "\"" +
                "\"aditInfo\":\"" + aditInfo + "\"" +
                "\"splpc\":\"" + splpc + "\"" +
                "\"vat\":\"" + vat + "\"" +
                "\"taxxpt\":\"" + taxxpt + "\"" +
                "\"svcpc\":\"" + svcpc + "\"" +
                "\"delngNo\":\"" + delngNo + "\"" +
                "\"trxId\":\"" + trxId + "\"" +
                "}";
    }

    public void setJSONString(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);
            if (json.has("isRegisted"))
                isRegisted = json.getString("isRegisted").equals("true");
            if (json.has("token"))
                token = json.getString("token");
            if (json.has("tmnId"))
                tmnId = json.getString("tmnId");
            if (json.has("serial"))
                serial = json.getString("serial");
            if (json.has("appId"))
                appId = json.getString("appId");
            if (json.has("mchtId"))
                mchtId = json.getString("mchtId");
            if (json.has("version"))
                version = json.getString("version");
            if (json.has("telNo"))
                telNo = json.getString("telNo");
            if (json.has("mchtName"))
                mchtName = json.getString("mchtName");
            if (json.has("trackId"))
                trackId = json.getString("trackId");
            if (json.has("cardCashSe"))
                cardCashSe = json.getString("cardCashSe");
            if (json.has("delngSe"))
                delngSe = json.getString("delngSe");
            if (json.has("setleSuccesAt"))
                setleSuccesAt = json.getString("setleSuccesAt");
            if (json.has("setleMssage"))
                setleMssage = json.getString("setleMssage");
            if (json.has("confmNo"))
                confmNo = json.getString("confmNo");
            if (json.has("regDate"))
                regDate = json.getString("regDate");
            if (json.has("cardNo"))
                cardNo = json.getString("cardNo");
            if (json.has("instlmtMonth"))
                instlmtMonth = json.getString("instlmtMonth");
            if (json.has("issuCmpnyCode"))
                issuCmpnyCode = json.getString("issuCmpnyCode");
            if (json.has("issuCmpnyNm"))
                issuCmpnyNm = json.getString("issuCmpnyNm");
            if (json.has("puchasCmpnyCode"))
                puchasCmpnyCode = json.getString("puchasCmpnyCode");
            if (json.has("puchasCmpnyNm"))
                puchasCmpnyNm = json.getString("puchasCmpnyNm");
            if (json.has("aditInfo"))
                aditInfo = json.getString("aditInfo");
            if (json.has("splpc"))
                splpc = json.getString("splpc");
            if (json.has("vat"))
                vat = json.getString("vat");
            if (json.has("taxxpt"))
                taxxpt = json.getString("taxxpt");
            if (json.has("svcpc"))
                svcpc = json.getString("svcpc");
            if (json.has("delngNo"))
                delngNo = json.getString("delngNo");
            if (json.has("trxId"))
                trxId = json.getString("trxId");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }


    public String getMchtAddr() {
        return mchtAddr;
    }

    public void setMchtAddr(String mchtAddr) {
        this.mchtAddr = mchtAddr;
    }

    public String getMchtBizNum() {
        return mchtBizNum;
    }

    public void setMchtBizNum(String mchtBizNum) {
        this.mchtBizNum = mchtBizNum;
    }

    public String getTrxId() {
        return trxId;
    }

    public void setTrxId(String trxId) {
        this.trxId = trxId;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isRegisted() {
        return isRegisted;
    }

    public void setRegisted(boolean registed) {
        isRegisted = registed;
    }

    public String getTmnId() {
        return tmnId;
    }

    public void setTmnId(String tmnId) {
        this.tmnId = tmnId;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getMchtId() {
        return mchtId;
    }

    public void setMchtId(String mchtId) {
        this.mchtId = mchtId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTelNo() {
        return telNo;
    }

    public void setTelNo(String telNo) {
        this.telNo = telNo;
    }

    public String getMchtName() {
        return mchtName;
    }

    public void setMchtName(String mchtName) {
        this.mchtName = mchtName;
    }

    public String getCardCashSe() {
        return cardCashSe;
    }

    public void setCardCashSe(String cardCashSe) {
        this.cardCashSe = cardCashSe;
    }

    public String getDelngSe() {
        return delngSe;
    }

    public void setDelngSe(String delngSe) {
        this.delngSe = delngSe;
    }

    public String getSetleSuccesAt() {
        return setleSuccesAt;
    }

    public void setSetleSuccesAt(String setleSuccesAt) {
        this.setleSuccesAt = setleSuccesAt;
    }

    public String getSetleMssage() {
        return setleMssage;
    }

    public void setSetleMssage(String setleMssage) {
        this.setleMssage = setleMssage;
    }

    public String getConfmNo() {
        return confmNo;
    }

    public void setConfmNo(String confmNo) {
        this.confmNo = confmNo;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getInstlmtMonth() {
        return instlmtMonth;
    }

    public void setInstlmtMonth(String instlmtMonth) {
        this.instlmtMonth = instlmtMonth;
    }

    public String getIssuCmpnyCode() {
        return issuCmpnyCode;
    }

    public void setIssuCmpnyCode(String issuCmpnyCode) {
        this.issuCmpnyCode = issuCmpnyCode;
    }

    public String getIssuCmpnyNm() {
        return issuCmpnyNm;
    }

    public void setIssuCmpnyNm(String issuCmpnyNm) {
        this.issuCmpnyNm = issuCmpnyNm;
    }

    public String getPuchasCmpnyCode() {
        return puchasCmpnyCode;
    }

    public void setPuchasCmpnyCode(String puchasCmpnyCode) {
        this.puchasCmpnyCode = puchasCmpnyCode;
    }

    public String getPuchasCmpnyNm() {
        return puchasCmpnyNm;
    }

    public void setPuchasCmpnyNm(String puchasCmpnyNm) {
        this.puchasCmpnyNm = puchasCmpnyNm;
    }

    public String getAditInfo() {
        return aditInfo;
    }

    public void setAditInfo(String aditInfo) {
        this.aditInfo = aditInfo;
    }

    public String getSplpc() {
        return splpc;
    }

    public void setSplpc(String splpc) {
        this.splpc = splpc;
    }

    public String getVat() {
        return vat;
    }

    public void setVat(String vat) {
        this.vat = vat;
    }

    public String getTaxxpt() {
        return taxxpt;
    }

    public void setTaxxpt(String taxxpt) {
        this.taxxpt = taxxpt;
    }

    public String getSvcpc() {
        return svcpc;
    }

    public void setSvcpc(String svcpc) {
        this.svcpc = svcpc;
    }

    public String getDelngNo() {
        return delngNo;
    }

    public void setDelngNo(String delngNo) {
        this.delngNo = delngNo;
    }
}
