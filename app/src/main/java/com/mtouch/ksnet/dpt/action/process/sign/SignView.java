package com.mtouch.ksnet.dpt.action.process.sign;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class SignView extends View
{
	ArrayList<Vertex> m_arVertex;
	Paint		m_PaintPen;
	Paint		m_PaintBackground;
	Path[][]	m_arPath;
	
	final int MIN_WIDTH		= 128;
	final int MIN_HEIGHT	= 64;
	
	public SignView( Context context, AttributeSet attrs, int defSytel )
	{
		super( context, attrs, defSytel );
		init();
	}
	
	public SignView( Context context, AttributeSet attrs )
	{
		super( context, attrs );
		init();
	}
	
	public SignView( Context context )
	{
		super( context );
		init();	
	}

	private void init()
	{
		m_arVertex = new ArrayList<Vertex>();
		
		m_PaintPen = new Paint();
		m_PaintPen.setColor(Color.BLACK);
		m_PaintPen.setStrokeWidth(1);
		m_PaintPen.setAntiAlias(true);
		
		m_PaintBackground = new Paint();
		m_PaintBackground.setColor(Color.WHITE);
	}	
	
	protected void onDraw(Canvas canvas)
    {
    	Rect clip = canvas.getClipBounds();
    	canvas.drawRect( 0, 0, getWidth(), getHeight(), m_PaintBackground);
    	   	
    	// ������ ��ȸ�ϸ鼭 �������� �մ´�.
		for ( int i=0; i<m_arVertex.size(); i++ )
		{
			if ( m_arVertex.get(i).draw )
			{
				if ( Rect.intersects( clip, getLineRect(i) ) )
				{
					canvas.drawLine( m_arVertex.get(i-1).x, m_arVertex.get(i-1).y, m_arVertex.get(i).x, m_arVertex.get(i).y, m_PaintPen);
				}				
			}
		}
	}	
	
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
    	int wMode, hMode;
    	int wSize, hSize;
    	int width, height;

    	width	= MIN_WIDTH;
    	wMode = MeasureSpec.getMode(widthMeasureSpec);
    	wSize = MeasureSpec.getSize(widthMeasureSpec);
    	switch( wMode )
    	{
    		case MeasureSpec.AT_MOST:
    			width = Math.min( wSize, width );
    			break;
    		case MeasureSpec.EXACTLY:
    		case MeasureSpec.UNSPECIFIED:
    			width = wSize;
    			break;
    	}
   
    	height	= MIN_HEIGHT;
    	hMode = MeasureSpec.getMode(heightMeasureSpec);
    	hSize = MeasureSpec.getSize(heightMeasureSpec);
    	switch( hMode )
    	{
    		case MeasureSpec.AT_MOST:
    			height = Math.min( hSize, height );
    			break;
    		case MeasureSpec.EXACTLY:
    		case MeasureSpec.UNSPECIFIED:
    			height = hSize;
    			break;
    	}    	
    	
    	setMeasuredDimension( width, height );
    }

    public boolean isSign() {
		if(m_arVertex.size() > 0) {
			return true;
		} else
		{
			return false;
		}
	}
    //
    public void clear()
    {
    	m_arVertex.clear();
    	invalidate();
    }
    
    //
    public Bitmap getSign()
    {
    	Bitmap bmpSign = Bitmap.createBitmap( getWidth(), getHeight(), Bitmap.Config.ARGB_8888 );
    	Canvas canvas = new Canvas(bmpSign);
    	//onDraw(canvas);
    	
    	Rect clip = canvas.getClipBounds();
    	canvas.drawRect( 0, 0, getWidth(), getHeight(), m_PaintBackground);
    	   	
    	//
		for ( int i=0; i< m_arVertex.size(); i++ )
		{
			if ( m_arVertex.get(i).draw )
			{
				if ( Rect.intersects( clip, getLineRect(i) ) )
				{
					canvas.drawLine( m_arVertex.get(i-1).x, m_arVertex.get(i-1).y, m_arVertex.get(i).x, m_arVertex.get(i).y, m_PaintPen);
				}				
			}
		}
    	
    	return bmpSign;    	
    }

	//
    public boolean onTouchEvent(MotionEvent event)
    {
    	if (event.getAction() == MotionEvent.ACTION_DOWN)
    	{
    		m_arVertex.add( new Vertex(event.getX(), event.getY(), false) );
    		return true;
    	}

    	if (event.getAction() == MotionEvent.ACTION_MOVE)
    	{
    		m_arVertex.add( new Vertex(event.getX(), event.getY(), true) );
    		invalidate();
    		return true;
    	}

    	return false;
    }

    //
    public int getPenColor()
    {
    	return m_PaintPen.getColor();
    }
    
    //
    public int getBackgroundColor()
    {
    	return m_PaintBackground.getColor();
    }
    

    private Rect getLineRect( int idx )
    {
    	Rect rect = new Rect();
    	Vertex prev = m_arVertex.get(idx-1);
    	Vertex now = m_arVertex.get(idx);
    	
    	rect.set( (int)Math.min(now.x, prev.x)-2, (int)Math.min(now.y, prev.y)-2, (int)Math.max(now.x, prev.x)+3, (int)Math.max(now.y, prev.y)+3 );
    	return rect;
    }
    
    //
    public void setPenColor(int _color)
	{
		m_PaintPen.setColor( _color );
		invalidate();
	}
	
    //
	public void setBackgroundColor(int _color)
	{
		m_PaintBackground.setColor( _color );
		invalidate();
	}
	
	//
	private class Vertex
	{
		float x;
		float y;
		boolean draw;
		
		Vertex(float ax, float ay, boolean ad)
		{
			x = ax;
			y = ay;
			draw = ad;
		}
	}

    private byte[] intToByte(int value)
    {
    	byte first  = (byte) ( (value & 0x000000FF ) );
        byte second = (byte) ( (value & 0x0000FF00 ) >>> 8 );
        byte third  = (byte) ( (value & 0x00FF0000 ) >>> 16 );
        byte forth  = (byte) ( (value & 0xFF000000 ) >>> 24 );
        
        byte[] result = new byte[] { forth, third, second, first } ;
        
        return result;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //  128 x 64 pixel, 1bit BITMAP
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    public byte[] get1BitBitmap()
    {
    	// 128x64
		Bitmap bmpSign = Bitmap.createScaledBitmap(getSign(), 128, 64, true);
	
    	int[] nBits = new int[256];
    	int   nBitsIndex = 0;
    	int   nBitPosition = 0;
    	for ( int y=bmpSign.getHeight()-1; y>=0; y-- )
    	{
    		for ( int x=0; x<bmpSign.getWidth(); x++ )
    		{
    			if( nBitPosition == 32)
				{
					nBitsIndex++;
					nBitPosition = 0;
				}  
    			
    			if( bmpSign.getPixel(x, y) == getBackgroundColor() )
        		{
        			switch ( nBitPosition )
            		{
            			case  0: nBits[nBitsIndex] |= 0x80000000; break;
            			case  1: nBits[nBitsIndex] |= 0x40000000; break;
            			case  2: nBits[nBitsIndex] |= 0x20000000; break;
            			case  3: nBits[nBitsIndex] |= 0x10000000; break;
            			case  4: nBits[nBitsIndex] |= 0x08000000; break;
            			case  5: nBits[nBitsIndex] |= 0x04000000; break;
            			case  6: nBits[nBitsIndex] |= 0x02000000; break;
            			case  7: nBits[nBitsIndex] |= 0x01000000; break;
            			case  8: nBits[nBitsIndex] |= 0x00800000; break;
            			case  9: nBits[nBitsIndex] |= 0x00400000; break;
            			case 10: nBits[nBitsIndex] |= 0x00200000; break;
            			case 11: nBits[nBitsIndex] |= 0x00100000; break;
            			case 12: nBits[nBitsIndex] |= 0x00080000; break;
            			case 13: nBits[nBitsIndex] |= 0x00040000; break;
            			case 14: nBits[nBitsIndex] |= 0x00020000; break;
            			case 15: nBits[nBitsIndex] |= 0x00010000; break;
            			case 16: nBits[nBitsIndex] |= 0x00008000; break;
            			case 17: nBits[nBitsIndex] |= 0x00004000; break;
            			case 18: nBits[nBitsIndex] |= 0x00002000; break;
            			case 19: nBits[nBitsIndex] |= 0x00001000; break;
            			case 20: nBits[nBitsIndex] |= 0x00000800; break;
            			case 21: nBits[nBitsIndex] |= 0x00000400; break;
            			case 22: nBits[nBitsIndex] |= 0x00000200; break;
            			case 23: nBits[nBitsIndex] |= 0x00000100; break;
            			case 24: nBits[nBitsIndex] |= 0x00000080; break;
            			case 25: nBits[nBitsIndex] |= 0x00000040; break;
            			case 26: nBits[nBitsIndex] |= 0x00000020; break;
            			case 27: nBits[nBitsIndex] |= 0x00000010; break;
            			case 28: nBits[nBitsIndex] |= 0x00000008; break;
            			case 29: nBits[nBitsIndex] |= 0x00000004; break;
            			case 30: nBits[nBitsIndex] |= 0x00000002; break;
            			case 31: nBits[nBitsIndex] |= 0x00000001; break;
            		}
        			// End: switch
        		}
    			// End: if( bmpSign.getPixel(x, y) == Color.WHITE )
    			
    			nBitPosition++;
    			
    		}
    		// End: for
    	}
    	// End: for
    	
    	byte[] bBmp = new byte[1086];
    	int    nBmpOffset = 62;
    	for ( int i=0; i<nBits.length; i++)
    	{
    		byte[] b = intToByte( nBits[i] );
    		System.arraycopy( b, 0, bBmp, nBmpOffset, 4 );
    		nBmpOffset += 4;
    	}
    	    	
    	// 1bit BMP ���
		bBmp[ 0] = (byte)0x42; bBmp[ 1] = (byte)0x4D; bBmp[ 2] = (byte)0x3E; bBmp[ 3] = (byte)0x04; bBmp[ 4] = (byte)0x00;		
	    bBmp[ 5] = (byte)0x00; bBmp[ 6]	= (byte)0x00; bBmp[ 7] = (byte)0x00; bBmp[ 8] = (byte)0x00; bBmp[ 9] = (byte)0x00;	    
	    bBmp[10] = (byte)0x3E; bBmp[11]	= (byte)0x00; bBmp[12] = (byte)0x00; bBmp[13] = (byte)0x00; bBmp[14] = (byte)0x28;	    
	    bBmp[15] = (byte)0x00; bBmp[16]	= (byte)0x00; bBmp[17] = (byte)0x00; bBmp[18] = (byte)0x80; bBmp[19] = (byte)0x00;	    	    
	    bBmp[20] = (byte)0x00; bBmp[21]	= (byte)0x00; bBmp[22] = (byte)0x40; bBmp[23] = (byte)0x00; bBmp[24] = (byte)0x00;	    
	    bBmp[25] = (byte)0x00; bBmp[26]	= (byte)0x01; bBmp[27] = (byte)0x00; bBmp[28] = (byte)0x01; bBmp[29] = (byte)0x00;	    
	    bBmp[30] = (byte)0x00; bBmp[31]	= (byte)0x00; bBmp[32] = (byte)0x00; bBmp[33] = (byte)0x00; bBmp[34] = (byte)0x00;	    
	    bBmp[35] = (byte)0x04; bBmp[36]	= (byte)0x00; bBmp[37] = (byte)0x00; bBmp[38] = (byte)0x00; bBmp[39] = (byte)0x00;	    
	    bBmp[40] = (byte)0x00; bBmp[41]	= (byte)0x00; bBmp[42] = (byte)0x00; bBmp[43] = (byte)0x00; bBmp[44] = (byte)0x00;	    
	    bBmp[45] = (byte)0x00; bBmp[46]	= (byte)0x00; bBmp[47] = (byte)0x00; bBmp[48] = (byte)0x00; bBmp[49] = (byte)0x00;    
	    bBmp[50] = (byte)0x00; bBmp[51]	= (byte)0x00; bBmp[52] = (byte)0x00; bBmp[53] = (byte)0x00; bBmp[54] = (byte)0x00;	    
	    bBmp[55] = (byte)0x00; bBmp[56]	= (byte)0x00; bBmp[57] = (byte)0x00; bBmp[58] = (byte)0xFF; bBmp[59] = (byte)0xFF;
	    bBmp[60] = (byte)0xFF; bBmp[61]	= (byte)0x00;
	    
		return bBmp;
    }
}
