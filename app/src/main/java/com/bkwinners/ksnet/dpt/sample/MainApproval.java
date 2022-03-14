package com.bkwinners.ksnet.dpt.sample;

public class MainApproval {

	public static void main(String[] args) {
		
		com.ksnet.interfaces.Approval approval = new com.ksnet.interfaces.Approval();

		
		java.nio.ByteBuffer bb = java.nio.ByteBuffer.allocateDirect(4096);
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		// �ű� ��ȣȭ ���� ����
		////////////////////////////////////////////////////////////////////////////////////////////////////
		/*
		bb.put((byte)0x02);                                             // STX
		bb.put("MS".getBytes());                                        // �ŷ�����
		bb.put("01".getBytes());                                        // ��������
		bb.put("0200".getBytes());                                      // ���б���		
		bb.put("N".getBytes());                                         // �ŷ�����
		bb.put("DPT0TEST03".getBytes());                                // �ܸ����ȣ
		for(int i=0; i< 4; i++) bb.put(" ".getBytes());                 // ��ü����
		for(int i=0; i<12; i++) bb.put(" ".getBytes());                 // �����Ϸù�ȣ
		bb.put("S".getBytes());                                         // POS Entry Mode
		for(int i=0; i<20; i++) bb.put(" ".getBytes());                 // �ŷ� ���� ��ȣ
		for(int i=0; i<20; i++) bb.put(" ".getBytes());                 // ��ȣȭ���� ���� ī�� ��ȣ
		bb.put("9".getBytes());                                         // ��ȣȭ����
		for(int i=0; i<16; i++) bb.put("#".getBytes());                 // S/W �𵨹�ȣ
		for(int i=0; i<16; i++) bb.put("#".getBytes());                 // ��� �𵨹�ȣ
		for(int i=0; i<40; i++) bb.put(" ".getBytes());                 // ��ȣȭ ����
		bb.put("4330280486944821=17072011025834200000".getBytes());     // Track II
		bb.put((byte)0x1C);                                             // FS
		bb.put("00".getBytes());                                        // �Һΰ���
		bb.put("000000001004".getBytes());                              // �ѱݾ�
		bb.put("000000000000".getBytes());                              // �����
		bb.put("000000000000".getBytes());                              // ����
		bb.put("000000000000".getBytes());                              // ���ޱݾ�
		bb.put("000000000000".getBytes());                              // �鼼�ݾ�
		bb.put("AA".getBytes());                                        // Working Key Index
		for(int i=0; i<16; i++) bb.put("0".getBytes());                 // ��й�ȣ
		bb.put("            ".getBytes());                              // ���ŷ����ι�ȣ
		bb.put("      ".getBytes());                                    // ���ŷ���������
		for(int i=0; i<163; i++) bb.put(" ".getBytes());                // ��������� ~ DCC ȯ����ȸ Data
		                                                                // EMV Data Length(4 bytes)
		                                                                // EMV Data
		bb.put("N".getBytes());                                         // ���ڼ��� ����
		
		//bb.put("S".getBytes());                                         // ���ڼ��� ����
		//bb.put("83".getBytes());                                        // ���ڼ��� ��ȣȭ Key Index
		//for(int i=0; i<16; i++) bb.put("0".getBytes());                 // ��ǰ�ڵ� �� ����
		//bb.put(String.format("%04d",  encBmpData.length()).getBytes()); // ���ڼ��� ����
		//bb.put(encBmpData.getBytes());                                  // ���ڼ���
		
		bb.put((byte)0x03);                                             // ETX
		bb.put((byte)0x0D);                                             // CR
		
		byte[] telegram = new byte[ bb.position() ];
		bb.rewind();
		bb.get( telegram );		
		
		byte[] requestTelegram = new byte[telegram.length + 4];
		String telegramLength = String.format("%04d", telegram.length);
		System.arraycopy(telegramLength.getBytes(), 0, requestTelegram, 0, 4              );
		System.arraycopy(telegram                 , 0, requestTelegram, 4, telegram.length);
		
		System.out.println("requestTelegram: [" + new String(requestTelegram, 0, requestTelegram.length) + "]");
		
		byte[] responseTelegram = new byte[2048];
		//int rtn = approval.request("192.168.90.35", 9562, 5, requestTelegram, responseTelegram, 10000);
		int rtn = approval.request("210.181.28.116", 9562, 5, requestTelegram, responseTelegram, 10000);
		
		System.out.println("rtn: [" + rtn + "]");
		
		if(rtn == -102 || rtn == -103 || rtn == -104)
		{
			int i = 0;
			for(i = responseTelegram.length-1; i>=0; i--) if(responseTelegram[i] != (byte)0x00) break;				
			byte[] tmp = new byte[i];
			System.arraycopy(responseTelegram, 0, tmp, 0, tmp.length);
			
			int inResponseIdx = 0;
			
			System.out.println("����: [" 			+ new String(tmp, inResponseIdx, 4 ) + "]");	inResponseIdx += 4;
			System.out.println("Stx: [" 		+ new String(tmp, inResponseIdx, 1 ) + "]");	inResponseIdx += 1;
			System.out.println("�ŷ�����: [" 		+ new String(tmp, inResponseIdx, 2 ) + "]");	inResponseIdx += 2;
			System.out.println("��������: [" 		+ new String(tmp, inResponseIdx, 2 ) + "]");	inResponseIdx += 2;
			System.out.println("��������: [" 		+ new String(tmp, inResponseIdx, 4 ) + "]");	inResponseIdx += 4;
			System.out.println("�ŷ�����: [" 		+ new String(tmp, inResponseIdx, 1 ) + "]");	inResponseIdx += 1;
			System.out.println("�ܸ����ȣ: [" 		+ new String(tmp, inResponseIdx, 10) + "]");	inResponseIdx += 10;
			System.out.println("��ü����: [" 		+ new String(tmp, inResponseIdx, 4 ) + "]");	inResponseIdx += 4;
			System.out.println("�����Ϸù�ȣ: ["		+ new String(tmp, inResponseIdx, 12) + "]");	inResponseIdx += 12;
			System.out.println("status: [" 		+ new String(tmp, inResponseIdx, 1 ) + "]");	inResponseIdx += 1;
			System.out.println("ǥ�������ڵ�: [" 	+ new String(tmp, inResponseIdx, 4 ) + "]");	inResponseIdx += 4;
			System.out.println("ī��������ڵ�: [" 	+ new String(tmp, inResponseIdx, 4 ) + "]");	inResponseIdx += 4;
			System.out.println("�ŷ��Ͻ�: ["		+ new String(tmp, inResponseIdx, 12) + "]");	inResponseIdx += 12;
			System.out.println("ī�� Type: [" 	+ new String(tmp, inResponseIdx, 1 ) + "]");	inResponseIdx += 1;
			System.out.println("Message1: [" 	+ new String(tmp, inResponseIdx, 16) + "]");	inResponseIdx += 16;
			System.out.println("Message2: [" 	+ new String(tmp, inResponseIdx, 16) + "]");	inResponseIdx += 16;
			System.out.println("���ι�ȣ: [" 		+ new String(tmp, inResponseIdx, 12) + "]");	inResponseIdx += 12;
			System.out.println("�ŷ�������ȣ: [" 	+ new String(tmp, inResponseIdx, 20) + "]");	inResponseIdx += 20;
			
			System.out.println("responseTelegram: [" + new String(tmp, 0, tmp.length) + "]");
		}
		*/
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		// ������ SI 240 ����
		////////////////////////////////////////////////////////////////////////////////////////////////////
		/*
		bb.put((byte)0x02);                                         // STX
		bb.put("JA".getBytes());                                    // �ŷ�����
		bb.put("DPT0TEST03".getBytes());                            // �ܸ����ȣ
		for(int i=0; i< 4; i++) bb.put(" ".getBytes());             // ��ü����
		for(int i=0; i<12; i++) bb.put(" ".getBytes());             // �����Ϸù�ȣ
		bb.put("S".getBytes());                                     // POS Entry Mode
		bb.put("****************=********************".getBytes()); // Track II
		bb.put((byte)0x1C);                                         // FS
		bb.put("00".getBytes());                                    // �Һΰ���
		bb.put("000001004".getBytes());                             // �ѱݾ�
		bb.put("000000000".getBytes());                             // �����
		bb.put("000000000".getBytes());                             // ����
		bb.put("000000000".getBytes());                             // ���ޱݾ�
		bb.put("AA".getBytes());                                    // Working Key Index
		for(int i=0; i<16; i++) bb.put("0".getBytes());             // ��й�ȣ
		bb.put("            ".getBytes());                          // ���ŷ����ι�ȣ
		bb.put("      ".getBytes());                                // ���ŷ���������
		for(int i=0; i<73; i++) bb.put(" ".getBytes());             // ��������� ~ �ſ�ī�� ����
		bb.put("N".getBytes());                                     // �ŷ�����
		                                                            // �ŷ����¿� ���� Data
		bb.put("N".getBytes());                                     // ���ڼ��� ����
		bb.put((byte)0x03);                                         // ETX
		bb.put((byte)0x0D);                                         // CR
		
		byte[] telegram = new byte[ bb.position() ];
		bb.rewind();
		bb.get( telegram );		
		
		byte[] requestTelegram = new byte[telegram.length + 4];
		String telegramLength = String.format("%04d", telegram.length);
		System.arraycopy(telegramLength.getBytes(), 0, requestTelegram, 0, 4              );
		System.arraycopy(telegram                 , 0, requestTelegram, 4, telegram.length);
		
		System.out.println("requestTelegram: [" + new String(requestTelegram, 0, requestTelegram.length) + "]");
		
		byte[] responseTelegram = new byte[2048];
		int rtn = approval.request("210.181.28.116", 9562, 4, requestTelegram, responseTelegram, 10000);
		*/
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		// ������ 204 ����
		////////////////////////////////////////////////////////////////////////////////////////////////////
		// �ſ�
		/*
		bb.put((byte)0x02);
		bb.put("NA".getBytes());
		bb.put("DPT0TEST03".getBytes());
		for(int i=0; i<8; i++) bb.put(" ".getBytes());
		for(int i=0; i<6; i++) bb.put(" ".getBytes());
		bb.put("S".getBytes());
		bb.put("****************=********************".getBytes());
		bb.put((byte)0x1C);
		bb.put("00".getBytes());
		bb.put("000001004".getBytes());
		bb.put("000000000".getBytes());
		bb.put("AA".getBytes());
		bb.put("0000000000000000".getBytes());
		for(int i=0; i<118; i++) bb.put(" ".getBytes());
		bb.put((byte)0x00);
		bb.put((byte)0x00);
		bb.put("".getBytes());
		bb.put((byte)0x1C);
		bb.put("".getBytes());
		bb.put((byte)0x03);
		bb.put((byte)0x0D);
		*/
		
		// ����
		/*
		bb.put((byte)0x02);
		bb.put("bq".getBytes());
		bb.put("DPT0TEST03".getBytes());
		for(int i=0; i<8; i++) bb.put(" ".getBytes());
		for(int i=0; i<6; i++) bb.put(" ".getBytes());
		bb.put("0".getBytes());
		bb.put("K".getBytes());		
		bb.put("01012345678                          ".getBytes());
		bb.put((byte)0x1C);
		bb.put("000006300".getBytes());
		bb.put("000000700".getBytes());
		bb.put("000000000".getBytes());
		bb.put("000007000".getBytes());
		bb.put("0".getBytes());
		for(int i=0; i<144; i++) bb.put(" ".getBytes());
		bb.put((byte)0x03);
		bb.put((byte)0x0D);
		*/
		/*
		byte[] telegram = new byte[ bb.position() ];
		bb.rewind();
		bb.get( telegram );		
		
		byte[] requestTelegram = new byte[telegram.length + 4];
		String telegramLength = String.format("%04d", telegram.length);
		System.arraycopy(telegramLength.getBytes(), 0, requestTelegram, 0, 4              );
		System.arraycopy(telegram                 , 0, requestTelegram, 4, telegram.length);
		
		System.out.println("requestTelegram: [" + new String(requestTelegram, 0, requestTelegram.length) + "]");
		
		byte[] responseTelegram = new byte[2048];
		int rtn = approval.request("210.181.28.116", 9531, 2, requestTelegram, responseTelegram, 10000);
		*/
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		// ������ 200 ����
		////////////////////////////////////////////////////////////////////////////////////////////////////
		// �ſ�
		/*
		bb.put((byte)0x02);
		bb.put("CA".getBytes());
		bb.put("DPT0TEST03".getBytes());
		for(int i=0; i<8; i++) bb.put(" ".getBytes());
		for(int i=0; i<6; i++) bb.put(" ".getBytes());
		bb.put("S".getBytes());
		bb.put("****************=********************".getBytes());
		bb.put((byte)0x1C);
		bb.put("00".getBytes());
		bb.put("000001004".getBytes());
		bb.put("000000000".getBytes());
		bb.put("AA".getBytes());
		bb.put("0000000000000000".getBytes());
		for(int i=0; i<110; i++) bb.put(" ".getBytes());
		bb.put((byte)0x03);
		bb.put((byte)0x0D);
		*/
		/*
		// ����
		bb.put((byte)0x02);
		bb.put("bq".getBytes());
		bb.put("DPT0TEST03".getBytes());
		for(int i=0; i<8; i++) bb.put(" ".getBytes());
		for(int i=0; i<6; i++) bb.put(" ".getBytes());
		bb.put("0".getBytes());
		bb.put("K".getBytes());		
		bb.put("01012345678                          ".getBytes());
		bb.put((byte)0x1C);
		bb.put("000006300".getBytes());
		bb.put("000000700".getBytes());
		bb.put("000000000".getBytes());
		bb.put("000007000".getBytes());
		bb.put("0".getBytes());
		for(int i=0; i<144; i++) bb.put(" ".getBytes());
		bb.put((byte)0x03);
		bb.put((byte)0x0D);
		
		
		byte[] telegram = new byte[ bb.position() ];
		bb.rewind();
		bb.get( telegram );		
		
		System.out.println("requestTelegram: [" + new String(telegram, 0, telegram.length) + "]");
		
		byte[] responseTelegram = new byte[2048];
		int rtn = approval.request("210.181.28.116", 9531, 1, telegram, responseTelegram, 10000);
		*/
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		// KICC ����
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		byte[] responseTelegram = new byte[4096];
		
		String hexString = "0245584E4731363138313649313D203030303134301C414450543054455354303523232323232323414E44524F4944202020202020202020234D4637232323232323232323234D4350415949432D4B494331303031231C41534543553334374D30303031323231303031363238303030303839444130303030416F4B4C74756A776F4B31416C4F69322F67416B59752F456C767265336452687446776341312B733163574F3133386C446F7A7538614A6F542B645163696A48321C30301C313030301C1C1C1C1C1C3130301C4E1C1C1C1C1C031B7B";
		
		byte[] requestTelegram = new byte[2048];
		
		requestTelegram = hexString.getBytes();
				//
		int rtn = approval.requestDiff("210.181.28.116", 9581, 10, requestTelegram, responseTelegram, 15 * 1000);
				



		if(rtn > 0 || rtn == -9 || rtn == -100) {
			// �ʿ��ϴٸ� �޺κ��� �ڸ���
			int i = 0;
			for(i=responseTelegram.length-1; i>=0; i--) if(responseTelegram[i] != (byte)0x00) break;				
			byte[] tmp = new byte[i];
			System.arraycopy(responseTelegram, 0, tmp, 0, tmp.length);
			System.out.println("responseTelegram: [" + new String(tmp, 0, tmp.length) + "]");
		}
	}

}
