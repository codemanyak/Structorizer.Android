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
 *      Description:    This class represents an "parallel statement" in a diagram.
 *
 ******************************************************************************************************
 *
 *      Revision List
 *
 *      Author          Date			Description
 *      ------		----			-----------
 *      Bob Fisch       2010.11.26              First Issue
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


public class Parallel extends Element
{
	
    public Vector<Subqueue> qs = new Vector<Subqueue>();

    private Rect r = new Rect();
    private int fullWidth = 0;
    private int maxHeight = 0;
	
    @Override
    public void setText(String _text)
    {

            Subqueue s = null;

            text.setText(_text);

            if(qs==null)
            {
                    qs = new Vector();
            }

            // we need at least one line
            if(text.count()>0)
            {
                int count = 10;
                try
                {
                    // retrieve the number of parallel tasks
                    count = Integer.valueOf(text.get(0).trim());
                }
                catch (java.lang.NumberFormatException e)
                {
                    //JOptionPane.showMessageDialog(null, "Unknonw number <"+text.get(0).trim()+">.\nSetting number of tasks to 10!", "Error", JOptionPane.ERROR_MESSAGE);
                    text = new StringList();
                    text.add("10");
                    count = 10;
                }

                // add subqueues
                while(count>qs.size())
                {
                        s=new Subqueue();
                        s.parent=this;
                        qs.add(s);
                }
                // remove subqueues
                while(count<qs.size())
                {
                        qs.removeElementAt(qs.size()-1);
                }
            }

    }

    @Override
    public void setText(StringList _textList)
    {
            Subqueue s = null;

            text=_textList;

            if(qs==null)
            {
                    qs = new Vector();
            }

            // we need at least one line
            if(text.count()>0)
            {
                    // retrieve the number of parallel tasks
                    int count = Integer.valueOf(text.get(0));
                    // add subqueues
                    while(count>qs.size())
                    {
                            s=new Subqueue();
                            s.parent=this;
                            qs.add(s);
                    }
                    // remove subqueues
                    while(count<qs.size())
                    {
                            qs.removeElementAt(qs.size()-1);
                    }
            }

    }

    public Parallel()
    {
            super();
    }

    public Parallel(String _strings)
    {
            super(_strings);
            setText(_strings);
    }

    public Parallel(StringList _strings)
    {
            super(_strings);
            setText(_strings);
    }

    public Rect prepareDraw(Canvas _canvas)
    {

            rect.top=0;
            rect.left=0;


            rect.right=Math.round(3*(E_PADDING/2));
            rect.bottom=4*Math.round(E_PADDING/2);

            // retrieve the number of parallel tasks
            int tasks = Integer.valueOf(text.get(0));


            Rect rtt = null;

            fullWidth=0;
            maxHeight=0;

            if (qs.size()!=0)
            {

                    for (int i=0;i<tasks;i++)
                    {
                       rtt=((Subqueue) qs.get(i)).prepareDraw(_canvas);
                       fullWidth=fullWidth+Math.max(rtt.right,getWidthOutVariables(_canvas,text.get(i+1))+Math.round(E_PADDING / 2));
                       if(maxHeight<rtt.bottom)
                       {
                            maxHeight=rtt.bottom;
                       }
                    }
            }

            rect.right=Math.max(rect.right,fullWidth)+1;
            rect.bottom=rect.bottom+maxHeight;

            return rect;
    }

    public void draw(Canvas _canvas, Rect _top_left)
    {
            // retrieve the number of parallel tasks
            int tasks = Integer.valueOf(text.get(0));

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

            // fill shape
            canvas.setColor(drawColor);
            myrect=_top_left.copy();
            myrect.left+=1;
            myrect.top+=1;
            //myrect.right-=1;
            canvas.fillRect(myrect);

            // draw shape
            myrect=_top_left.copy();
            myrect.bottom=_top_left.top+2*Canvas.stringHeight("")+4*Math.round(E_PADDING / 2);

            int y=myrect.top+E_PADDING;
            int a=myrect.left+Math.round((myrect.right-myrect.left) / 2);
            int b=myrect.top;
            int c=myrect.left+fullWidth-1;
            int d=myrect.bottom-1;
            int x=Math.round(((y-b)*(c-a)+a*(d-b))/(d-b));

            // draw comment
            if(Element.E_SHOWCOMMENTS==true && !comment.getText().trim().equals(""))
            {
                    canvas.setBackground(E_COMMENTCOLOR);
                    canvas.setColor(E_COMMENTCOLOR);

                    Rect someRect = myrect.copy();

                    someRect.left+=2;
                    someRect.top+=2;
                    someRect.right=someRect.left+4;
                    someRect.bottom-=2;

                    canvas.fillRect(someRect);
            }


            // draw lines
            canvas.setColor(Color.BLACK);
            int lineWidth=0;
            Rect rtt = null;

            for(int i = 0; i<tasks;i++)
            {
                    rtt=((Subqueue) qs.get(i)).prepareDraw(_canvas);
                    lineWidth=lineWidth+Math.max(rtt.right,getWidthOutVariables(_canvas,text.get(i+1))+Math.round(E_PADDING / 2));
            }

            // corners
            myrect=_top_left.copy();

            canvas.moveTo(myrect.left,myrect.bottom-2*Math.round(E_PADDING/2));
            canvas.lineTo(myrect.left+2*Math.round(E_PADDING/2),myrect.bottom);

            canvas.moveTo(myrect.left,myrect.top+2*Math.round(E_PADDING/2));
            canvas.lineTo(myrect.left+2*Math.round(E_PADDING/2),myrect.top);

            canvas.moveTo(myrect.right-2*Math.round(E_PADDING/2),myrect.top);
            canvas.lineTo(myrect.right,myrect.top+2*Math.round(E_PADDING/2));

            canvas.moveTo(myrect.right-2*Math.round(E_PADDING/2),myrect.bottom);
            canvas.lineTo(myrect.right,myrect.bottom-2*Math.round(E_PADDING/2));

            // horizontal lines
            canvas.moveTo(myrect.left,myrect.top+2*Math.round(E_PADDING/2));
            canvas.lineTo(myrect.right,myrect.top+2*Math.round(E_PADDING/2));

            canvas.moveTo(myrect.left,myrect.bottom-2*Math.round(E_PADDING/2));
            canvas.lineTo(myrect.right,myrect.bottom-2*Math.round(E_PADDING/2));

            // draw children
            myrect=_top_left.copy();
            myrect.top=_top_left.top+2*Math.round(E_PADDING/2);
            myrect.bottom=_top_left.bottom-2*Math.round(E_PADDING/2);

            if (qs.size()!=0)
            {

                    for(int i = 0;i <tasks ; i++)
                    {
                            rtt=((Subqueue) qs.get(i)).prepareDraw(_canvas);

                            if(i==tasks-1)
                            {
                                    myrect.right=_top_left.right;
                            }
/*
                            else if((i!=qs.size()-1) || (!(this.parent.parent.getClass().getCanonicalName().equals("Root"))))
                            {
                                    myrect.right=myrect.left+Math.max(rtt.right,_canvas.stringWidth(text.get(i+1)+Math.round(E_PADDING / 2)));
                            }
*/
                            else
                            {
                                    myrect.right=myrect.left+Math.max(rtt.right,getWidthOutVariables(_canvas,text.get(i+1))+Math.round(E_PADDING / 2))+1;
                            }

                            // draw child
                            ((Subqueue) qs.get(i)).draw(_canvas,myrect);

                            // draw bottom up line
                            /*
                            if((i!=qs.size()-2)&&(i!=tasks))
                            {
                                    canvas.moveTo(myrect.right-1,myrect.top);
                                    int mx=myrect.right-1;
                                    int my=myrect.top-Canvas.stringHeight("");
                                    int sx=mx;
                                    int sy=Math.round((sx*(by-ay)-ax*by+ay*bx)/(bx-ax));
                                    canvas.lineTo(sx,sy+1);
                            }
                             *
                             */

                            myrect.left=myrect.right-1;

                    }
            }

            canvas.setColor(Color.BLACK);
            canvas.drawRect(_top_left);
            
            selectRect=rect.copy();
    }

    @Override
    public Element selectElementByCoord(int _x, int _y)
    {
            Element selMe = super.selectElementByCoord(_x,_y);
            Element selCh = null;

            for(int i = 0;i<qs.size();i++)
            {
                    Element pre = ((Subqueue) qs.get(i)).selectElementByCoord(_x,_y);
                    if(pre!=null)
                    {
                            selCh = pre;
                    }
            }

            if(selCh!=null)
            {
                    selected=false;
                    selMe = selCh;
            }

            return selMe;
    }

    public void setSelected(boolean _sel)
    {
            selected=_sel;
            /* Quatsch !
            for(int i = 0;i<qs.size();i++)
            {
                    ((Subqueue) qs.get(i)).setSelected(_sel);
            }
            */
    }

    public Element copy() // Problem here???
    {
            Element ele = new Parallel(this.getText().getText());
            //ele.setText(this.getText().copy());
            ele.setComment(this.getComment().copy());
            ele.setColor(this.getColor());
            ((Parallel) ele).qs.clear();
            for(int i=0;i<qs.size();i++)
            {
                    Subqueue ss = (Subqueue) ((Subqueue) this.qs.get(i)).copy();
                    ss.parent=ele;
                    ((Parallel) ele).qs.add(ss);
            }

            return ele;
    }
	
}
