/*
    Structorizer
    A little tool which you can use to create Nassi-Schneiderman Diagrams (NSD)

    Copyright (C) 2009  Bob Fisch

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or any
    later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package lu.fisch.structorizer.elements;

/******************************************************************************************************
 *
 *      Author:         Bob Fisch
 *
 *      Description:    This class represents an "IF statement" in a diagram.
 *
 ******************************************************************************************************
 *
 *      Revision List
 *
 *      Author          Date			Description
 *      ------			----			-----------
 *      Bob Fisch       2007.12.10      First Issue
 *
 ******************************************************************************************************
 *
 *      Comment:		/
 *
 ******************************************************************************************************///


import lu.fisch.awt.Color;


import lu.fisch.graphics.*;
import lu.fisch.utils.*;

public class Alternative extends Element {

	public Subqueue qFalse = new Subqueue();
	public Subqueue qTrue = new Subqueue();
	
	private Rect rTrue = new Rect();
	private Rect rFalse = new Rect();

	public Alternative()
	{
		super();
		qFalse.parent=this;
		qTrue.parent=this;
	}
	
	public Alternative(String _strings)
	{
		super(_strings);
		qFalse.parent=this;
		qTrue.parent=this;
		setText(_strings);
	}
	
	public Alternative(StringList _strings)
	{
		super(_strings);
		qFalse.parent=this;
		qTrue.parent=this;
		setText(_strings);
	}
	
	public Rect prepareDraw(Canvas _canvas)
	{
		rect.top=0;
		rect.left=0;
		
		rect.right=2*Math.round(E_PADDING/2);

		rect.right=Math.round(2*(E_PADDING));

                // prepare the sub-queues
		rFalse=qFalse.prepareDraw(_canvas);
		rTrue=qTrue.prepareDraw(_canvas);

                // the upper left point of the corner
                double cx = 0;
                double cy = text.count()*Canvas.stringHeight("")+4*Math.round(E_PADDING/2);
                // the the lowest point of the triangle
                double ax =  rTrue.right-rTrue.left;
                //System.out.println("AX : "+ax);
                double ay =  0;
                // coefficient of the left droite
                double coeffleft = (cy-ay)/(cx-ax);


                // init
                int choice = -1;
                double lowest = 100000;
                
		for(int i=0;i<text.count();i++)
		{

                        /* old code
                        if(rect.right<_canvas.stringWidth((String) text.get(i))+4*Math.round(E_PADDING))
			{
				rect.right=_canvas.stringWidth((String) text.get(i))+4*Math.round(E_PADDING);
			}
                        */
                    
                        // bottom line of the text
                        double by = 4*Math.round(E_PADDING/2)-Math.round(E_PADDING/3)+(text.count()-i-1)*Canvas.stringHeight(text.get(i));
                        // part on the left side
                        double leftside = by/coeffleft+ax-ay/coeffleft;
                        // the the bottom right point of this text line
                        int textWidth = Canvas.stringWidth(text.get(i));
                        double bx = textWidth+2*Math.round(E_PADDING/2) + leftside;
                        //System.out.println("LS : "+leftside);
                        
                        // check if this is the one we need to do calculations
                        double coeff = (by-ay)/(bx-ax);

                        // the point height we need
                        double y = text.count()*Canvas.stringHeight(text.get(i))+4*Math.round(E_PADDING/2);
                        double x = y/coeff + ax - ay/coeff;
                        //System.out.println(i+" => "+coeff+" --> "+String.valueOf(x));

                        if (coeff<lowest && coeff>0)
                        {
                            // remember it
                            lowest = coeff;
                            choice = i;
                        }
		}
                if (lowest!=100000)
                {
                    // the point height we need
                    double y = text.count()*Canvas.stringHeight("")+4*Math.round(E_PADDING/2);
                    double x = y/lowest + ax - ay/lowest;
                    rect.right = (int) Math.round(x);
                    //System.out.println("C => "+lowest+" ---> "+rect.right);
                }
                else
                {
                    rect.right=4*Math.round(E_PADDING/2);
                }
		
		rect.bottom=4*Math.round(E_PADDING/2)+text.count()*Canvas.stringHeight("");
		
		rect.right=Math.max(rect.right,rTrue.right+rFalse.right);
		rect.bottom+=Math.max(rTrue.bottom,rFalse.bottom);
		
		return rect;
	}
	
	public void draw(Canvas _canvas, Rect _top_left)
	{
		Rect myrect = new Rect();
		Color drawColor = getColor();
		int a;
		int b;
		int c;
		int d;
		int x;
		int y;
		int wmax;
		int p;
		int w;
		
		if (selected==true)
		{
                if(waited==true) { drawColor=Element.E_WAITCOLOR; }
                else { drawColor=Element.E_DRAWCOLOR; }
		}
	
		Canvas canvas = _canvas;
		canvas.setColor(drawColor);
		
		myrect=_top_left.copy();
		myrect.bottom-=1;
		canvas.fillRect(myrect);

		rect=_top_left.copy();
		myrect.bottom=_top_left.top+text.count()*Canvas.stringHeight("")+4*Math.round(E_PADDING / 2);
		y=myrect.top+E_PADDING;
		a=myrect.left+Math.round((myrect.right-myrect.left) / 2);
		b=myrect.top;
		c=myrect.left+rTrue.right-1;
		d=myrect.bottom-1;
		x=Math.round(((y-b)*(c-a)+a*(d-b))/(d-b));
/*
		wmax=0;
		for(int i=0;i<text.count();i++)
		{
			if (wmax<_canvas.stringWidth(text.get(i)))
			{
				wmax = _canvas.stringWidth(text.get(i));
			}
		}
*/

                // the upper left point of the corner
                double cx = 0;
                double cy = text.count()*Canvas.stringHeight("")+4*Math.round(E_PADDING/2);
                // upper right corner
                double dx = _top_left.right-_top_left.left;
                double dy = cy;
                // the the lowest point of the triangle
                double ax =  rTrue.right-rTrue.left;
                double ay =  0;
                // coefficient of the left droite
                double coeffleft = (cy-ay)/(cx-ax);
                double coeffright = (dy-ay)/(dx-ax);

                // draw text
		for(int i=0;i<text.count();i++)
		{
			String mytext = this.text.get(i);

                        // bottom line of the text
                        double by = 4*Math.round(E_PADDING/2)-Math.round(E_PADDING/3)+(text.count()-i-1)*Canvas.stringHeight("");
                        // part on the left side
                        double leftside = by/coeffleft+ax-ay/coeffleft;
                        // the the bottom right point of this text line
                        double bx = by/coeffright+ax-ay/coeffright;
                        /* debugging output
                        canvas.setColor(Color.RED);
                        canvas.fillRect(new Rect(
                                myrect.left+(int) cx-2, myrect.bottom-(int) cy-2,
                                myrect.left+(int) cx+2, myrect.bottom-(int) cy+2)
                        );
                        canvas.moveTo(myrect.left+(int) leftside, myrect.bottom-(int) by);
                        canvas.lineTo(myrect.left+(int) bx, myrect.bottom-(int) by);
                        */
                        int boxWidth = (int) (bx-leftside);
                        int textWidth = Canvas.stringWidth(text.get(i));

                        canvas.setColor(Color.BLACK);
                        writeOutVariables(canvas,
                            _top_left.left + (int) leftside + (int) (boxWidth - textWidth)/2,
                            _top_left.top+Math.round(E_PADDING / 3)+(i+1)*Canvas.stringHeight(""),
                            mytext
                        );

                        /*
			if(rotated==false)
			{
				canvas.setColor(Color.BLACK);
				writeOutVariables(canvas,
								  x-Math.round(_canvas.stringWidth(text)/2),
								_top_left.top+Math.round(E_PADDING / 3)+(i+1)*fm.getHeight(),
								text
								);  	
			}
			else
			{
				// draw rotated
				
				// coloredTextOut(text[i],vars,colors,_canvas,_top_left.Left+(E_PADDING div 2)+i*_canvas.TextHeight(text[i]),
				// _top_left.bottom-(E_PADDING div 2),rotated);
				
			}
                         */
		}
		
		// draw symbols
		canvas.writeOut(myrect.left+Math.round(E_PADDING / 2),
						myrect.bottom-Math.round(E_PADDING / 2),preAltT);
		canvas.writeOut(myrect.right-Math.round(E_PADDING / 2)-_canvas.stringWidth(preAltF),
						myrect.bottom-Math.round(E_PADDING / 2),preAltF);
		
		
		
		// draw comment
		if(Element.E_SHOWCOMMENTS==true && !comment.getText().trim().equals(""))
		{
			canvas.setColor(E_COMMENTCOLOR);
			
			Rect someRect = myrect.copy();
			
			someRect.left+=2;
			someRect.top+=2;
			someRect.right=someRect.left+4;
			someRect.bottom-=2;
			
			canvas.fillRect(someRect);
		}
		
		// draw triangle
		canvas.setColor(Color.BLACK);
		canvas.moveTo(myrect.left,myrect.top);
		canvas.lineTo(myrect.left+rTrue.right-1,myrect.bottom-1);
		canvas.lineTo(myrect.right-1,myrect.top);
		
		// draw children
		myrect=_top_left.copy();
		myrect.top=_top_left.top+Canvas.stringHeight("")*text.count()+4*Math.round(E_PADDING / 2)-1;
		myrect.right=myrect.left+rTrue.right-1;
		
		qTrue.draw(_canvas,myrect);
		
		myrect.left=myrect.right;
		myrect.right=_top_left.right;
		qFalse.draw(_canvas,myrect);
		
		
		myrect=_top_left.copy();
		canvas.setColor(Color.BLACK);
		canvas.drawRect(myrect);
		
		selectRect=rect.copy();
	}

	public Element selectElementByCoord(int _x, int _y)
	{
		Element selMe = super.selectElementByCoord(_x,_y);
		Element selT = qTrue.selectElementByCoord(_x,_y);
		Element selF = qFalse.selectElementByCoord(_x,_y);
		if(selT!=null) 
		{
		 selected=false;
		 selMe = selT;
		}
		else if (selF != null)
		{
		 selected=false;
		 selMe=selF;
		}
		
		return selMe;
	}
	
	public void setSelected(boolean _sel)
	{
		selected=_sel;
		//qFalse.setSelected(_sel);
		//qTrue.setSelected(_sel);
	}
	
	public Element copy()
	{
		Element ele = new Alternative(this.getText().copy());
		ele.setComment(this.getComment().copy());
		ele.setColor(this.getColor());
		((Alternative) ele).qTrue=(Subqueue) this.qTrue.copy();
		((Alternative) ele).qFalse=(Subqueue) this.qFalse.copy();
		((Alternative) ele).qTrue.parent=ele;
		((Alternative) ele).qFalse.parent=ele;
		return ele;
	}
	
	
	
		
	
	
}
