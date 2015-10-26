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
 *      Description:    This class represents an "REPEAT loop" in a diagram.
 *
 ******************************************************************************************************
 *
 *      Revision List
 *
 *      Author          Date			Description
 *      ------			----			-----------
 *      Bob Fisch       2007.12.12      First Issue
 *
 ******************************************************************************************************
 *
 *      Comment:		/
 *
 ******************************************************************************************************///

import java.util.Vector;

import lu.fisch.awt.*;

import lu.fisch.graphics.*;
import lu.fisch.utils.*;

public class Repeat extends Element {
	
	public Subqueue q = new Subqueue();
	
	private Rect r = new Rect();
	
	public Repeat()
	{
		super();
		q.parent=this;
	}
	
	public Repeat(String _strings)
	{
		super(_strings);
		q.parent=this;
		setText(_strings);
	}
	
	public Repeat(StringList _strings)
	{
		super(_strings);
		q.parent=this;
		setText(_strings);
	}
	
	public Rect prepareDraw(Canvas _canvas)
	{
		rect.top=0;
		rect.left=0;
		
		rect.right=2*Math.round(E_PADDING/2);
		
		rect.right=Math.round(2*(E_PADDING/2));
		for(int i=0;i<text.count();i++)
		{
			if(rect.right<getWidthOutVariables(_canvas,text.get(i))+2*Math.round(E_PADDING/2))
			{
				rect.right=getWidthOutVariables(_canvas,text.get(i))+2*Math.round(E_PADDING/2);
			}
		}
		
		rect.bottom=2*Math.round(E_PADDING/2)+text.count()*Canvas.stringHeight("");
		
		r=q.prepareDraw(_canvas);
		
		rect.right=Math.max(rect.right,r.right+E_PADDING);
		rect.bottom+=r.bottom;		
		return rect;
	}
	
	public void draw(Canvas _canvas, Rect _top_left)
	{
		Rect myrect = new Rect();
		Color drawColor = getColor();
		int p;
		int w;
		
		if (selected==true)
		{
                    if(waited==true) { drawColor=Element.E_WAITCOLOR; }
                    else { drawColor=Element.E_DRAWCOLOR; }
		}
		
		Canvas canvas = _canvas;
		canvas.setBackground(drawColor);
		canvas.setColor(drawColor);
		
		rect=_top_left.copy();
		
		// draw shape
		myrect=_top_left.copy();
		canvas.setColor(Color.BLACK);
		myrect.bottom=_top_left.bottom;
		myrect.top=myrect.bottom-Canvas.stringHeight("")*text.count()-2*Math.round(E_PADDING / 2);
		canvas.drawRect(myrect);
		
		myrect=_top_left.copy();
		myrect.right=myrect.left+Element.E_PADDING;
		canvas.drawRect(myrect);
		
		// fill shape
		canvas.setColor(drawColor);
		myrect.left=myrect.left;
		myrect.top=myrect.top;
		myrect.bottom=myrect.bottom;
		myrect.right=myrect.right;
		canvas.fillRect(myrect);
		
		myrect=_top_left.copy();
		myrect.bottom=_top_left.bottom;
		myrect.top=myrect.bottom-Canvas.stringHeight("")*text.count()-2*Math.round(E_PADDING / 2);
		myrect.left=myrect.left;
		myrect.top=myrect.top;
		myrect.bottom=myrect.bottom;
		myrect.right=myrect.right;
		canvas.fillRect(myrect);
	
		// draw upper & left line
		canvas.getGraphics().setColor(Color.BLACK);
		canvas.getGraphics().drawLine(_top_left.left, _top_left.top, _top_left.right, _top_left.top);
		canvas.getGraphics().drawLine(_top_left.left,_top_left.top,_top_left.left,_top_left.bottom);
		canvas.getGraphics().drawLine(_top_left.left, _top_left.bottom, _top_left.right, _top_left.bottom);
		canvas.getGraphics().drawLine(_top_left.right,_top_left.top,_top_left.right,_top_left.bottom);
		
		// draw comment
		if(Element.E_SHOWCOMMENTS==true && !comment.getText().trim().equals(""))
		{
			canvas.setBackground(E_COMMENTCOLOR);
			canvas.setColor(E_COMMENTCOLOR);
			
			Rect someRect = _top_left.copy();
			
			someRect.left+=2;
			someRect.top+=2;
			someRect.right=someRect.left+4;
			someRect.bottom-=1;
			
			canvas.fillRect(someRect);
		}
		
		
		// draw text
		for(int i=0;i<text.count();i++)
		{
			String text = this.text.get(i);
			
			canvas.setColor(Color.BLACK);
			writeOutVariables(canvas,
							  _top_left.left+Math.round(E_PADDING / 2),
							myrect.top+Math.round(E_PADDING / 2)+(i+1)*Canvas.stringHeight(""),
							text
							);  	
		}
		
		// draw children
		myrect.bottom=myrect.top;
		myrect.top=_top_left.top;
		myrect.left=myrect.left+E_PADDING-2;
		myrect.right=_top_left.right;
		q.draw(_canvas,myrect);
		
		selectRect=rect.copy();
	}
	
	public Element selectElementByCoord(int _x, int _y)
	{
		Element selMe = super.selectElementByCoord(_x,_y);
		Element sel = q.selectElementByCoord(_x,_y);
		if(sel!=null) 
		{
			selected=false;
			selMe = sel;
		}
		
		return selMe;
	}
	
	public void setSelected(boolean _sel)
	{
		selected=_sel;
		//q.setSelected(_sel);
	}
	
	public Element copy()
	{
		Element ele = new Repeat(this.getText().copy());
		ele.setComment(this.getComment().copy());
		ele.setColor(this.getColor());
		((Repeat) ele).q=(Subqueue) this.q.copy();
		((Repeat) ele).q.parent=ele;
		return ele;
	}
	
	
	
}
