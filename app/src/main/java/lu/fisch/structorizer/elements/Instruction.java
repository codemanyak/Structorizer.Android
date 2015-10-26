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
 *      Description:    This class represents an "instruction" in a diagram.
 *
 ******************************************************************************************************
 *
 *      Revision List
 *
 *      Author          Date			Description
 *      ------			----			-----------
 *      Bob Fisch       2007.12.09      First Issue
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

public class Instruction extends Element {

	public boolean rotated = false;

	public Instruction()
	{
		super();
	}
	
	public Instruction(String _strings)
	{
		super(_strings);
		setText(_strings);
	}
	
	public Instruction(StringList _strings)
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
		
		
		if(rotated==false)
		{
			rect.right=Math.round(2*(Element.E_PADDING/2));
			for(int i=0;i<text.count();i++)
			{
				if(rect.right<getWidthOutVariables(_canvas,text.get(i))+1*Element.E_PADDING)
				{
					rect.right=getWidthOutVariables(_canvas,text.get(i))+1*Element.E_PADDING;
				}
			}
			rect.bottom=2*Math.round(Element.E_PADDING/2)+text.count()*Canvas.stringHeight("");
		}
		else
		{
			// draw rotated
			/*
			rect.bottom:=2*(E_PADDING div 2);
			for i:=0 to text.count-1 do
				if (rect.bottom<coloredTextOutWidth(text[i],vars,colors,_canvas)+2*(E_PADDING div 2))
				then rect.bottom:=coloredTextOutWidth(text[i],vars,colors,_canvas)+2*(E_PADDING div 2);

			rect.right:=2*(E_PADDING div 2)+text.count*_canvas.TextHeight('O');
			*/
		}
		
		return rect;
	}

	public void draw(Canvas _canvas, Rect _top_left)
	{
		Rect myrect = new Rect();
		Color drawColor = getColor();
			
		if (selected==true)
		{
			drawColor=Element.E_DRAWCOLOR;
		}
		
		rect=_top_left.copy();
		
		Canvas canvas = _canvas;
		canvas.setBackground(drawColor);
		canvas.setColor(drawColor);
		
		myrect=_top_left.copy();
		
		canvas.fillRect(myrect);
				
		// draw comment
		if(Element.E_SHOWCOMMENTS==true && !comment.getText().trim().equals(""))
		{
			canvas.setBackground(E_COMMENTCOLOR);
			canvas.setColor(E_COMMENTCOLOR);
			
			myrect.left+=2;
			myrect.top+=2;
			myrect.right=myrect.left+4;
			myrect.bottom-=1;
			
			canvas.fillRect(myrect);
		}
		
		for(int i=0;i<text.count();i++)
		{
			String text = this.text.get(i);
			text = BString.replace(text, "<--","<-");
			if(rotated==false)
			{
				canvas.setColor(Color.BLACK);
				writeOutVariables(canvas,
								  _top_left.left+Math.round(Element.E_PADDING / 2),
								  _top_left.top+Math.round(Element.E_PADDING / 2)+(i+1)*Canvas.stringHeight(""),
								  text
								  );  	
				/*canvas.writeOut(  _top_left.left+Math.round(Element.E_PADDING / 2),
								_top_left.top+Math.round(Element.E_PADDING / 2)+(i+1)*Canvas.stringHeight(""),
								text
								);  	/**/
			}
			else
			{
				// draw rotated
				/*
				coloredTextOut(text[i],vars,colors,_canvas,_top_left.Left+(E_PADDING div 2)+i*_canvas.TextHeight(text[i]),
							  _top_left.bottom-(E_PADDING div 2),rotated);
				*/
			}
		}
		canvas.setColor(Color.BLACK);
		canvas.drawRect(_top_left);
		
		selectRect=rect.copy();
	}
	
	public Element copy()
	{
		Instruction ele = new Instruction(this.getText().copy());
		ele.setComment(this.getComment().copy());
		ele.setColor(this.getColor());
		return ele;
	}
}
