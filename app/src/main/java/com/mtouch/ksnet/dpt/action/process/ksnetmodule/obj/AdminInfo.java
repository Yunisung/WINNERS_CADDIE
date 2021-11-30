package com.mtouch.ksnet.dpt.action.process.ksnetmodule.obj;

import android.util.Base64;

import java.util.HashMap;

/**
 * Created by 상완 on 2017-01-22.
 */
public class AdminInfo {

    /**승인정보
    PlayType , TelegramType, DPTID , BIZNO, BranchNM, PosEntry, TotalAmount, Amount, ServicAmount, TaxAmount, FreeAmount, Filler, SignTrans, PayType, CardType, AuthNum, Authdate
     **/
    private byte[] PlayType;
    private byte[] TelegramType;
    private byte[] TransType; //거래구분 , IC/MS/HK/PC
    private byte[] WorkType;

    public void setDPTID(byte[] DPTID) {
        this.DPTID = DPTID;
    }

    private byte[] DPTID;
    private byte[] BIZNO;  //승일일자
    private byte[] BranchNM;  //가맹점명
    private byte[] BranchBoss; //점주명
    private byte[] BranchAddr; //가맹점주소
    private byte[] BranchPhone; //가맹점전화번호

    private byte[] PosEntry;      //Pos Entry Mode
    private byte[] TotalAmount;
    private byte[] ServicAmount;
    private byte[] TaxAmount;
    private byte[] FreeAmount;
    private byte[] Filler;
    private byte[] SignTrans;
    private byte[] PayType;
    private byte[] CardType;
    private byte[] Amount;
    private byte[] AuthNum;   //승인번호
    private byte[] Authdate;  //승일일자
    private byte[] ReceiptNo;  //현금영수증번호

    private byte[] ksnet_server_ip;  // KSNET 접속 아이피
    private byte[] ksnet_server_port;  // KSNET 접속 포트
    private byte[] ksnet_telegrametype;  // KSNET TELEGRAME TYPE
    private byte[] ksnet_timeout;  // KSNET 서버 타임 아웃

    private byte[] trackId;


    public AdminInfo(HashMap<String, byte[]> AdminInfo_Hash)
    {
        this.PlayType = AdminInfo_Hash.get("PlayType");
        this.TelegramType = AdminInfo_Hash.get("TelegramType");
        this.DPTID = AdminInfo_Hash.get("DPTID");
        if(this.DPTID == null) this.DPTID = "DPT0TEST03".getBytes();
        this.BIZNO = AdminInfo_Hash.get("BIZNO");
        this.BranchNM = AdminInfo_Hash.get("BranchNM");
        this.BranchBoss = AdminInfo_Hash.get("Boss");
        this.BranchAddr = AdminInfo_Hash.get("Addr");
        this.BranchPhone = AdminInfo_Hash.get("Phone");
        this.PosEntry = AdminInfo_Hash.get("PosEntry");
        this.TotalAmount = AdminInfo_Hash.get("TotalAmount");
        this.ServicAmount = AdminInfo_Hash.get("ServicAmount");
        this.TaxAmount = AdminInfo_Hash.get("TaxAmount");
        this.FreeAmount = AdminInfo_Hash.get("FreeAmount");
        this.Amount = AdminInfo_Hash.get("Amount");
        this.Filler = AdminInfo_Hash.get("Filler");
        this.SignTrans = AdminInfo_Hash.get("SignTrans");
        this.PayType = AdminInfo_Hash.get("PayType");
        this.CardType = AdminInfo_Hash.get("CardType");
        this.AuthNum = AdminInfo_Hash.get("AuthNum");
        this.Authdate = AdminInfo_Hash.get("Authdate");
        this.ReceiptNo = AdminInfo_Hash.get("ReceiptNo"); //현금영수증 키인 정보
        this.WorkType = AdminInfo_Hash.get("WorkType"); //포인트 기능 추가 후 업무구분 추가
        if( this.WorkType == null )  this.WorkType = "01".getBytes();
        this.TransType = AdminInfo_Hash.get("TransType");
        this.trackId = AdminInfo_Hash.get("TrackId");

        this.ksnet_server_ip = AdminInfo_Hash.get("ksnet_server_ip");
        this.ksnet_server_port = AdminInfo_Hash.get("ksnet_server_port");
        this.ksnet_telegrametype = AdminInfo_Hash.get("ksnet_telegrametype");
        this.ksnet_timeout = AdminInfo_Hash.get("ksnet_timeout");
    }



    public byte[] getPlayType()
    {
        return this.PlayType;
    }
    public byte[] getTelegramType()
    {
        return this.TelegramType;
    }
    public byte[] getDPTID(){ return this.DPTID; }
    public byte[] getPosEntry()    {        return this.PosEntry;    }
    public byte[] getTotalAmount()
    {
        return this.TotalAmount;
    }
    public byte[] getServicAmount()
    {
        return this.ServicAmount;
    }
    public byte[] getTaxAmount()
    {
        return this.TaxAmount;
    }
    public byte[] getFreeAmount()
    {
        return this.FreeAmount;
    }
    public byte[] getAmount()
    {
        return this.Amount;
    }
    public byte[] getFiller()    {        return this.Filler;    }
    public byte[] getSignTrans()
    {
        return this.SignTrans;
    }
    public byte[] getSignTransBase64()
    {
        return  Base64.encode(SignTrans, Base64.DEFAULT);
    }

    public byte[] getPayType()
    {
        return this.PayType;
    }
    public byte[] getCardType()
    {
        return this.CardType;
    }
    public byte[] getAuthNum()
    {
        return this.AuthNum;
    }
    public byte[] getAuthDate()
    {
        return this.Authdate;
    }
    public byte[] getBIZNO()
    {
        return this.BIZNO;
    }
    public byte[] getBranchName()
    {
        return this.BranchNM;
    }
    public byte[] getReceiptNo()
    {
        return this.ReceiptNo;
    }
    public byte[] getBranchBoss()
    {
        return this.BranchBoss;
    }
    public byte[] getBranchAddr()
    {
        return this.BranchAddr;
    }
    public byte[] getBranchPhone()
    {
        return this.BranchPhone;
    }
    public byte[] getWorkType(){return  this.WorkType;}
    public byte[] getTransType(){return  this.TransType;}

    public void addFiller(byte[] addData)
    {
        this.Filler = ( new String(addData) ).getBytes();
    }
    public void setTelegramType(byte[] telegramType)
    {
        this.TelegramType = telegramType;
    }
    public void setTransType(byte[] transType)
    {
        this.TransType = transType;
    }
    public void setWorkType(byte[] workType)
    {
        this.WorkType = workType;
    }

    public void setSignTrans(byte[] SignTrans)
    {
        this.SignTrans = SignTrans;
    }

    public byte[] getTrackId() {
        return trackId;
    }

    public AdminInfo setPlayType(byte[] playType) {
        PlayType = playType;
        return this;
    }

    public AdminInfo setBIZNO(byte[] BIZNO) {
        this.BIZNO = BIZNO;
        return this;
    }

    public AdminInfo setBranchNM(byte[] branchNM) {
        BranchNM = branchNM;
        return this;
    }

    public AdminInfo setBranchBoss(byte[] branchBoss) {
        BranchBoss = branchBoss;
        return this;
    }

    public AdminInfo setBranchAddr(byte[] branchAddr) {
        BranchAddr = branchAddr;
        return this;
    }

    public AdminInfo setBranchPhone(byte[] branchPhone) {
        BranchPhone = branchPhone;
        return this;
    }

    public AdminInfo setPosEntry(byte[] posEntry) {
        PosEntry = posEntry;
        return this;
    }

    public AdminInfo setTotalAmount(byte[] totalAmount) {
        TotalAmount = totalAmount;
        return this;
    }

    public AdminInfo setServicAmount(byte[] servicAmount) {
        ServicAmount = servicAmount;
        return this;
    }

    public AdminInfo setTaxAmount(byte[] taxAmount) {
        TaxAmount = taxAmount;
        return this;
    }

    public AdminInfo setFreeAmount(byte[] freeAmount) {
        FreeAmount = freeAmount;
        return this;
    }

    public AdminInfo setFiller(byte[] filler) {
        Filler = filler;
        return this;
    }

    public AdminInfo setPayType(byte[] payType) {
        PayType = payType;
        return this;
    }

    public AdminInfo setCardType(byte[] cardType) {
        CardType = cardType;
        return this;
    }

    public AdminInfo setAmount(byte[] amount) {
        Amount = amount;
        return this;
    }

    public AdminInfo setAuthNum(byte[] authNum) {
        AuthNum = authNum;
        return this;
    }

    public AdminInfo setAuthdate(byte[] authdate) {
        Authdate = authdate;
        return this;
    }

    public AdminInfo setReceiptNo(byte[] receiptNo) {
        ReceiptNo = receiptNo;
        return this;
    }

    public AdminInfo setKsnet_server_ip(byte[] ksnet_server_ip) {
        this.ksnet_server_ip = ksnet_server_ip;
        return this;
    }

    public AdminInfo setKsnet_server_port(byte[] ksnet_server_port) {
        this.ksnet_server_port = ksnet_server_port;
        return this;
    }

    public AdminInfo setKsnet_telegrametype(byte[] ksnet_telegrametype) {
        this.ksnet_telegrametype = ksnet_telegrametype;
        return this;
    }

    public AdminInfo setKsnet_timeout(byte[] ksnet_timeout) {
        this.ksnet_timeout = ksnet_timeout;
        return this;
    }

    public AdminInfo setTrackId(byte[] trackId) {
        this.trackId = trackId;
        return this;
    }

    public void clearFiller()
    {
        this.Filler = new byte[30];
    }

    public String getKsnetServerIp() { return new String(ksnet_server_ip) ;}
    public String getKsnetServerPort() { return new String(ksnet_server_port) ;}
    public String getKsnetTelegrameType() { return new String(ksnet_telegrametype) ;}
    public String getKsnetTimeout() { return new String(ksnet_timeout) ;}




}
