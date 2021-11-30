package com.mtouch.ksnet.dpt.action.process.util;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.mtouch.ksnet.dpt.common.Utils;

import java.io.PrintStream;
import java.util.HashMap;

/**
 * Created by parksuwon on 2018-02-01.
 */

public class KsnetParsingByte {

    public static final int IDX_STX = 0;     //STX 영역 인덱스
    public static final int IDX_LEN = 1;      //Length 영역 인덱스
    public static final int IDX_COMMAND = 3; //Command 영역 인덱스
    public static final int IDX_DATA = 4;     //Data영역 인덱스




    public static int MakeReceiveData(byte[] r_data) {



        //데이터분할 방지
        try {
            Thread.sleep(50);


        //2nd Gen시 2nd 응답값과 DEL 응답이 붙어서 들어옴 - 수정
//        if (_idxRecvData > 3) {
         int Length = KsnetUtils.byte2Int(r_data[1]) * 0xff + KsnetUtils.byte2Int(r_data[2]);
       //  if(Length >= r_data.length) return 0;

         Log.d("Receive" , "Receive ETX  Length : " + Length + "/"+ r_data.length );
         //데이터 길이가 전달된 길이와 맞는지 체크
        //데이터마지막 -1 위치의 값이 0x03 ETX값인지 체크
        if (r_data!=null && r_data[Length+2] == (byte)0x03)
              return 1;



        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static byte GetCommandID(byte[] t_data) {
        byte cmd;
            /*
            if (t_data == null || t_data.length < SIZE_MIN_TOTAL) return (byte) 0x01;
            if (t_data[IDX_STX] != (byte) 0x02) return (byte) 0x02;
            if (t_data[_idxRecvData - 2] != (byte) 0x03) return (byte) 0x03; //ETX까지 확인인
            */
        cmd = t_data[IDX_COMMAND];
        return cmd;
    }

    public boolean CheckCommandError(byte[] bArr) {
        System.out.println("::::::::::::::::::::::::::::::Start CheckCommandError:::::::::::::::::::::::::::::::::::::::::::::::");
        if (bArr.length == 6 && bArr[0] == 2 && bArr[bArr.length - 2] == 3 && bArr[3] == 21) {
            System.out.println("::::::::::::::::::::::::::::::Start CheckCommandError::::::6byte NAK Command");
        }
        return false;
    }


    /*
    public int MakeReceiveData(byte[] arrby, int _idxRecvData, byte[] _recvData, View.OnClickListener listener) {
        int n;
        byte[] arrby2;
        byte[] arrby3;
        int n2;
        System.arraycopy((Object) arrby, (int) 0, (Object) _recvData, (int) _idxRecvData, (int) arrby.length);
        PrintStream printStream = System.out;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[\uc774\ubca4\ud2b8 \uc218\uc2e0]::::::[_recvData]::::::: \n\n::::::::::::[hex]");
        stringBuilder.append(Utils.stringToHex((String) new String(Utils.byteToString((byte[]) _recvData, (int) 0, (int) (_idxRecvData += arrby.length)).getBytes())));
        stringBuilder.append("\n[idx]");
        stringBuilder.append(_idxRecvData);
        printStream.println(stringBuilder.toString());
        for (int i = 0; i != (n2 = _idxRecvData) && n2 > 3 && (arrby3 = _recvData)[0] == 2 && _idxRecvData >= (n = 4 + (256 * Utils.byteToInt((byte) arrby3[1]) + Utils.byteToInt((byte) _recvData[2]))) && (arrby2 = _recvData)[n - 2] == 3 && arrby2[n - 1] == Utils.LRC((byte[]) Utils.byteToSubByte((byte[]) arrby2, (int) 0, (int) n)); ++i) {
            PrintStream printStream2 = System.out;
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("[\uc774\ubca4\ud2b8 \ub370\uc774\ud130 \uc218\uc2e0\uc644\ub8cc]::::::[\ubc84\ud37c\uc218\uc2e0 \uae38\uc774 = ");
            stringBuilder2.append(_idxRecvData);
            stringBuilder2.append("]::::::: \n");
            printStream2.println(stringBuilder2.toString());
            if (CheckCommandError(Utils.byteToSubByte((byte[]) _recvData, (int) 0, (int) n))) {
                System.out.println("::::::::::::::[\ub9ac\ub354\uae30 \uc751\ub2f5\uc624\ub958 \ubc1c\uc0dd! \uacb0\uc81c \uc885\ub8cc]::::::: \n");
//               clearTempBuffer();
                if (listener != null)
                    listener.onClick(null);
                return 0;
            }
//            if (ReadyCommandID(this.GetCommandID(Utils.byteToSubByte((byte[]) _recvData, (int) 0, (int) n)))) {
            Thread thread = new Thread((Runnable) new ThreadSerialWrite(this.GetCommandID(Utils.byteToSubByte((byte[]) _recvData, (int) 0, (int) n))));
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
//                cleaReadBuffer(n);

            try {
                Thread.sleep((long) 10L);
                continue;
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
//            }
            return 0;
        }
        return 0;
    }





    class ThreadSerialWrite implements Runnable {
        byte cmd;

        ThreadSerialWrite(byte b) {
            this.cmd = b;
        }

        public void run() {
            Intent intent;
            System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::Start ThreadSerialWrite::::::::::::::::::::::::::::::::::::::::::::::::");
            HashMap hashMap = new HashMap();
            if (this.cmd == -48) {
                hashMap.put("RequestDongleInfo", Utils.byteToString(TabMain._recvData, 36, 1));
                if (TabMain.this.isDebugging.booleanValue()) {
                    System.out.println("[수신]:::::RequestDongleInfo:::::");
                }
            } else if (TabMain.this.isDebugging.booleanValue()) {
                System.out.println("[수신]:::::::::::No Command ");
            }
            if (hashMap.containsKey("ICCardInOut")) {
                if (((String) hashMap.get("ICCardInOut")).equals("INS") && TabMain.this.isDebugging.booleanValue()) {
                    System.out.println("[수신]:::::INS:::::");
                }
                ((String) hashMap.get("ICCardInOut")).equals("DEL");
            }
            if (hashMap.containsKey("RequestDongleInfo")) {
                if (((String) hashMap.get("RequestDongleInfo")).equals("O")) {
                    TabMain tabMain = TabMain.this;
                    tabMain.mApp = (StateSetting) tabMain.getApplication();
                    if (TabMain.this.mApp.GetIsBootFirst().booleanValue()) {
                        TabMain.this.ReaderModelNum = new String(Utils.byteToSubByte(TabMain._recvData, 4, 16));
                        TabMain.this.mApp.setReaderModelName(TabMain.this.ReaderModelNum);
                        TabMain.this.mApp.setSWModelName(TabMain.this.SWModelNum);
                        System.out.println("#####상태정보요청 성공");
                        new Thread(new Runnable() {
                            public void run() {
                                TabMain.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        TextView textView = TabMain.this.tvBranchPhone;
                                        textView.setText("[SW] " + TabMain.this.SWModelNum + " [Reader] " + TabMain.this.ReaderModelNum);
                                    }
                                });
                            }
                        }).start();
                        TabMain.this.CDT.cancel();
                        TabMain.this.adBuild.cancel();
                        TabMain.this.clearTempBuffer();
                        TabMain.this.mApp.setIsBootFirst(false);
                    } else if (!TabMain.this.mApp.GetIsBootFirst().booleanValue()) {
                        System.out.println("#####상태정보요청 성공22");
                        if (TabMain.this.CDT != null) {
                            System.out.println("#####Timer 해지22");
                            TabMain.this.adBuild.cancel();
                            TabMain.this.CDT.cancel();
                            TabMain.this.clearTempBuffer();
                        }
                        TabMain.this.clearTempBuffer();
                        if (TabMain.this.isChkCredit.booleanValue()) {
                            System.out.println("#####isCHkCredit 트루");
                            if (new Configuration(TabMain.this).getStaffInfo().length > 0) {
                                intent = new Intent(TabMain.this, SetStaffBranchList.class);
                                intent.putExtra("BranchID", TabMain.this.SelectedBranchID);
                                intent.putExtra("BranchDPT", TabMain.this.SelectedBranchDPT);
                                intent.putExtra("BranchNAME", TabMain.this.SelectedBranchName);
                                intent.putExtra("Boss", TabMain.this.SelectedBranchBoss);
                                intent.putExtra("Addr", TabMain.this.SelectedBranchAddr);
                                intent.putExtra("Phone", TabMain.this.SelectedBranchPhone);
                                intent.putExtra("BtnSelName", "Credit");
                            } else {
                                intent = new Intent(TabMain.this, PayCreditStep1.class);
                                intent.putExtra("StaffID", "");
                                intent.putExtra("BranchID", TabMain.this.SelectedBranchID);
                                intent.putExtra("BranchDPT", TabMain.this.SelectedBranchDPT);
                                intent.putExtra("BranchNAME", TabMain.this.SelectedBranchName);
                                intent.putExtra("Boss", TabMain.this.SelectedBranchBoss);
                                intent.putExtra("Addr", TabMain.this.SelectedBranchAddr);
                                intent.putExtra("Phone", TabMain.this.SelectedBranchPhone);
                            }
                            System.out.println("#####PayCreditStep1 클릭 ");
                            TabMain.this.startActivity(intent);
                            Boolean unused = TabMain.this.isChkCredit = false;
                        }
                    }
                } else if (((String) hashMap.get("RequestDongleInfo")).equals("X")) {
                    TabMain.this.clearTempBuffer();
                }
            }
            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (TabMain.this.isDebugging.booleanValue()) {
                System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::End ThreadSerialWrite:::::::::::::::::::::::::::::::::::::::::::::::");
            }
        }
    }

*/

}
