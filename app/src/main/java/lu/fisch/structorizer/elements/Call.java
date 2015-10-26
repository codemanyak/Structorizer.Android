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
 *      Description:    This class represents an "call" in a diagram.
 *
 ******************************************************************************************************
 *
 *      Revision List
 *
 *      Author          Date			Description
 *      ------			----			-----------
 *      Bob Fisch       2007.12.13      First Issue
 *
 ******************************************************************************************************
 *
 *      Comment:		/
 *
 ******************************************************************************************************///

import lu.fisch.awt.Color;
import lu.fisch.graphics.Canvas;
import lu.fisch.graphics.Rect;
import lu.fisch.utils.BString;
import lu.fisch.utils.StringList;
import android.graphics.Paint.FontMetrics;

public class Call extends Instruction {
	
	public Call()
	{
		super();
	}
	
	public Call(String _strings)
	{
		super(_strings);
		setText(_strings);
	}
	
	public Call(StringList _strings)
	{
		super(_strings);
		setText(_strings);
	}
	
	
	public Rect prepareDraw(Canvas _canvas)
	{
		rect.top=0;
		rect.left=0;
		rect.right=0;
		rect.bottom=0;
		
		rect.right=Math.round(2*(E_PADDING/2));
		
		for(int i=0;i<text.count();i++)
		{
			if(rect.right<getWidthOutVariables(_canvas,text.get(i))+4*E_PADDING)
			{
				rect.right=getWidthOutVariables(_canvas,text.get(i))+4*E_PADDING;
			}
		}
		rect.bottom=2*Math.round(E_PADDING/2)+text.count()*Canvas.stringHeight("");

		return rect;
	}
	
	public void draw(Canvas _canvas, Rect _top_left)
	{
		Rect myrect = new Rect();
		Color drawColor = getColor();
		
		if (selected==true)
		{
			drawColor=E_DRAWCOLOR;
		}
		
		rect=_top_left.copy();
		
		Canvas canvas = _canvas;
		canvas.setColor(drawColor);
		
		myrect=_top_left.copy();
		
		canvas.fillRect(myrect);
		
		// draw comment
		if(Element.E_SHOWCOMMENTS==true && !comment.getText().trim().equals(""))
		{
			canvas.setColor(E_COMMENTCOLOR);
			
			Rect someRect = _top_left.copy();
			
			someRect.left+=2;
			someRect.top+=2;
			someRect.right=someRect.left+4;
			someRect.bottom-=1;
			
			canvas.fillRect(someRect);
		}
		
		
		for(int i=0;i<text.count();i++)
		{
			String text = this.text.get(i);
			text = BString.replace(text, "<--","<-");
			canvas.setColor(Color.BLACK);
			writeOutVariables(canvas,
							  _top_left.left+2*Math.round(E_PADDING / 2),
							_top_left.top+Math.round(E_PADDING / 2)+(i+1)*Canvas.stringHeight(""),
							text
							);  	
		}
		
		canvas.moveTo(_top_left.left+Math.round(E_PADDING / 2),_top_left.top);
		canvas.lineTo(_top_left.left+Math.round(E_PADDING / 2),_top_left.bottom);
		canvas.moveTo(_top_left.right-Math.round(E_PADDING / 2),_top_left.top);
		canvas.lineTo(_top_left.right-Math.round(E_PADDING / 2),_top_left.bottom);
		
		canvas.setColor(Color.BLACK);
		canvas.drawRect(_top_left);
		
		selectRect=rect.copy();
	}
	
	public Element copy()
	{
		Element ele = new Call(this.getText().copy());
		ele.setComment(this.getComment().copy());
		ele.setColor(this.getColor());
		return ele;
	}
	

	
	
}
