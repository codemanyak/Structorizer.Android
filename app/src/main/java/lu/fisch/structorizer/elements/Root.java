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
 *      Description:    This class represents the "root" of a diagram or the program/sub itself.
 *
 ******************************************************************************************************
 *
 *      Revision List
 *
 *      Author          Date		Description
 *      ------		----		-----------
 *      Bob Fisch       2007.12.09      First Issue
 *	Bob Fisch	2008.04.18      Added analyser
 *
 ******************************************************************************************************
 *
 *      Comment:		/
 *
 ******************************************************************************************************///


import java.util.Vector;
import java.util.Stack;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.File;

import android.graphics.Point;
import android.util.Log;

import lu.fisch.awt.*;

import lu.fisch.graphics.*;
import lu.fisch.utils.*;
public class Root extends Element {

	// some fields
	public boolean isNice = true;
	public boolean isProgram = true;
	public boolean hasChanged = false;
	public boolean hightlightVars = false;
	public Subqueue children = new Subqueue();

	public int height = 0;
	public int width = 0;
	public Point offset = new Point(0,0);

	private Stack undoList = new Stack();
	private Stack redoList = new Stack();

	public String filename = "";

	// variables
	public StringList variables = new StringList();
	public Vector errors = new Vector();
	private StringList rootVars = new StringList();

	// error checks for analyser
	public static boolean check1 = false;
	public static boolean check2 = false;
	public static boolean check3 = false;
	public static boolean check4 = false;
	public static boolean check5 = false;
	public static boolean check6 = false;
	public static boolean check7 = false;
	public static boolean check8 = false;
	public static boolean check9 = false;
	public static boolean check10 = false;
	public static boolean check11 = false;
	public static boolean check12 = false;
	public static boolean check13 = false;

    private Vector<Updater> updaters = new Vector<Updater>();

    public void addUpdater(Updater updater)
    {
        updaters.add(updater);
    }

    public void removeUpdater(Updater updater)
    {
        updaters.remove(updater);
    }

    public void setOffset(Point offset)
    {
    	this.offset=offset;
    }
    
    public Point getOffset()
    {
    	return offset;
    }

	public Root()
	{
		super(StringList.getNew("???"));
		setText(StringList.getNew("???"));
		children.parent=this;
	}

	public Root(StringList _strings)
	{
		super(_strings);
		setText(_strings);
		children.parent=this;
	}

	public Rect prepareDraw(Canvas _canvas)
	{
		Rect subrect = new Rect();

		rect.top=0;
		rect.left=0;



		if(isNice==true)
		{
			rect.right=2*E_PADDING;
			for(int i=0;i<text.count();i++)
			{
				int w = getWidthOutVariables(_canvas,text.get(i));
				if(rect.right<w+2*E_PADDING)
				{
					rect.right=w+2*E_PADDING;
				}
			}
			rect.bottom=3*E_PADDING+text.count()*Canvas.stringHeight("");
		}
		else
		{
			rect.right=2*E_PADDING;
			for(int i=0;i<text.count();i++)
			{
				if(rect.right<getWidthOutVariables(_canvas,text.get(i))+2*Math.round(E_PADDING/2))
				{
					rect.right=getWidthOutVariables(_canvas,text.get(i))+2*Math.round(E_PADDING/2);
				}
			}
			rect.bottom=2*Math.round(E_PADDING/2)+text.count()*Canvas.stringHeight("");
		}


		subrect=children.prepareDraw(_canvas);

		if(isNice==true)
		{
			rect.right=Math.max(rect.right,subrect.right+2*Element.E_PADDING);
		}
		else
		{
			rect.right=Math.max(rect.right,subrect.right);
		}

		rect.bottom+=subrect.bottom;
		this.width=rect.right-rect.left;
		this.height=rect.bottom-rect.top;

		return rect;
	}

	public void drawBuffered(Canvas _canvas, Rect _top_left)
	{
		/*
		// save reference to output canvas
		Canvas origCanvas = _canvas;
		// create a new image (buffer) to draw on
		BufferedImage bufferImg = new BufferedImage(_top_left.right+1,_top_left.bottom+1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D bufferGraphics = (Graphics2D) bufferImg.getGraphics();
		_canvas = new Canvas(bufferGraphics);


		draw(_canvas,_top_left);

		// draw buffer to output canvas
		origCanvas.draw(bufferImg,0,0);

		// free up the buffer an clean memory
		bufferImg=null;
		System.gc();*/
		draw(_canvas,_top_left);
	}

	public void draw(Canvas _canvas, Rect _top_left)
	{
		Rect myrect = new Rect();
		Color drawColor = getColor();

		if(text.count()==0)
		{
			text.add("???");
		}
		else if ( ((String)text.get(0)).trim().equals("") )
		{
			text.delete(0);
			text.insert("???",0);
		}

		rect=_top_left.copy();

		if(isNice==false)
		{
			drawColor=Color.WHITE;
		}
		else
		{
			drawColor=Color.LIGHT_GRAY;
		}

		drawColor=getColor();

		if(selected==true)
		{
                if(waited==true) { drawColor=Element.E_WAITCOLOR; }
                else { drawColor=Element.E_DRAWCOLOR; }
		}

		// draw background
		myrect=_top_left.copy();


		Canvas canvas = _canvas;

		// erase background
		canvas.setBackground(drawColor);
		canvas.setColor(drawColor);
		canvas.fillRect(myrect);

		/*
		 if(isNice=false) then _canvas.pen.Width:=3;
		 _canvas.pen.Color:=clBlack;
		 _canvas.brush.Color:=drawColor;
		 if(isProgram=true) then  _canvas.Rectangle(rect)
		 else if(isNice=false) then
		 begin
		 _canvas.pen.Color:=clNone;
		 _canvas.Rectangle(rect);
		 end
		 else _canvas.RoundRect(rect.Left,rect.Top,rect.Right,rect.Bottom,30,30); //(rect.right-rect.left) div 3,(rect.Bottom-rect.Top) div 3);
		 _canvas.pen.Width:=1;
		 */

		// draw comment
		if(E_SHOWCOMMENTS==true && !comment.getText().trim().equals(""))
		{
			canvas.setBackground(E_COMMENTCOLOR);
			canvas.setColor(E_COMMENTCOLOR);

			myrect.left+=2;
			myrect.top+=1;
			myrect.right=myrect.left+4;
			myrect.bottom-=2;

			canvas.fillRect(myrect);
		}



		// draw text
		if(isNice==true)
		{
			for(int i=0;i<text.count();i++)
			{
				canvas.setColor(Color.BLACK);
				writeOutVariables(canvas,
								  rect.left+E_PADDING,
							      rect.top+(i+1)*Canvas.stringHeight("")+E_PADDING,
								  (String) text.get(i)
								  );
			}
		}
		else
		{
			for(int i=0;i<text.count();i++)
			{
				canvas.setColor(Color.BLACK);
				canvas.writeOut(  rect.left+Math.round(E_PADDING/2),
								rect.top+(i+1)*Canvas.stringHeight("")+Math.round(E_PADDING/2),
								(String) text.get(i)
								);
			}
		}


		if(isNice==true)
		{
			rect.top=_top_left.top+Canvas.stringHeight("")*text.count()+2*E_PADDING;
			rect.bottom-=E_PADDING;
			rect.left=_top_left.left+E_PADDING;
			rect.right-=E_PADDING;
		}
		else
		{
			rect.top=_top_left.top+Canvas.stringHeight("")*text.count()+2*Math.round(E_PADDING/2);
			rect.left=_top_left.left;
		}

		children.draw(_canvas,rect);

		// draw box arround
		canvas.setColor(Color.BLACK);
		canvas.drawRect(_top_left);


		// draw thick line
		if(isNice==false)
		{
			rect.top=_top_left.top+Canvas.stringHeight("")*text.count()+2*Math.round(E_PADDING/2)-1;
			rect.left=_top_left.left;
			canvas.drawRect(rect);
		}


		if(isProgram==false)
		{
			rect=_top_left.copy();
			canvas.setColor(Color.WHITE);
			canvas.drawRect(rect);
			canvas.setColor(Color.BLACK);
			rect=_top_left.copy();
			canvas.roundRect(rect);
		}

		rect=_top_left.copy();
		
		selectRect=rect.copy();
	}

    @Override
    public Element selectElementByCoord(int _x, int _y)
    {
    	//Log.d("bob","Hit on ("+_x+","+_y+")");
        Element selMe = super.selectElementByCoord(_x,_y);
        Element selCh = children.selectElementByCoord(_x,_y);
        if(selCh!=null)
        {
                selected=false;
                return selCh;
        }
        else
        {
                return selMe;
        }
    }

    @Override
    public Element getElementByCoord(int _x, int _y)
    {
            Element selMe = super.getElementByCoord(_x,_y);
            Element selCh = children.getElementByCoord(_x,_y);
            if(selCh!=null)
            {
                    return selCh;
            }
            else
            {
                    return selMe;
            }
    }

    public boolean checkChild(Element _child, Element _parent)
    {
            Element tmp = _child;
            boolean res = false;
            if(tmp != null)
            {
                    while ((tmp.parent!=null)&&(res==false))
                    {
                            if(tmp.parent==_parent)
                            {
                                    res = true;
                            }
                            tmp=tmp.parent;
                    }
            }
            return res;
    }

    public void removeElement(Element _ele)
    {
            if(_ele != null)
            {
                    _ele.selected=false;
                    if ( !_ele.getClass().getSimpleName().equals("Subqueue") &&
                             !_ele.getClass().getSimpleName().equals("Root"))
                    {
                            ((Subqueue) _ele.parent).removeElement(_ele);
                            hasChanged=true;
                    }
            }
    }

    public void addAfter(Element _ele, Element _new)
    {
            if(_ele!=null && _new!=null)
            {
                    if (_ele.getClass().getSimpleName().equals("Subqueue"))
                    {
                            ((Subqueue) _ele).addElement(_new);
                            _new.parent=_ele;
                            _ele.selected=false;
                            _new.selected=true;
                            hasChanged=true;
                    }
                    else if (_ele.parent.getClass().getSimpleName().equals("Subqueue"))
                    {
                            int i = ((Subqueue) _ele.parent).children.indexOf(_ele);
                            ((Subqueue) _ele.parent).children.insertElementAt(_new, i+1);
                            _new.parent=_ele.parent;
                            _ele.selected=false;
                            _new.selected=true;
                            hasChanged=true;
                    }
                    else
                    {
                            // this case should never happen!
                    }

            }
    }

    public void addBefore(Element _ele, Element _new)
    {
            if(_ele!=null && _new!=null)
            {
                    if (_ele.getClass().getSimpleName().equals("Subqueue"))
                    {
                            ((Subqueue) _ele).addElement(_new);
                            _new.parent=_ele;
                            _ele.selected=false;
                            _new.selected=true;
                            hasChanged=true;
                    }
                    else if (_ele.parent.getClass().getSimpleName().equals("Subqueue"))
                    {
                            int i = ((Subqueue) _ele.parent).children.indexOf(_ele);
                            ((Subqueue) _ele.parent).children.insertElementAt(_new, i);
                            _new.parent=_ele.parent;
                            _ele.selected=false;
                            _new.selected=true;
                            hasChanged=true;
                    }
                    else
                    {
                            // this case should never happen!
                    }

            }
    }

    public Rect prepareDraw(Graphics _g)
    {
        Canvas canvas = new Canvas(_g);

        return this.prepareDraw(canvas);
    }

    public Rect draw(Graphics _g, Point point, Updater updater)
    {
        setDrawPoint(point);

        /*
        final Updater myUpdater = updater;
        new Thread(
            new Runnable()
            {
                public void run()
                {
                    // inform updaters
                    for(int u=0;u<updaters.size();u++)
                    {
                        if(updaters.get(u)!=myUpdater)
                        {
                            updaters.get(u).update();
                        }
                    }
                }
            }
        ).start();/**/

        // inform updaters
        for(int u=0;u<updaters.size();u++)
        {
            if(updaters.get(u)!=updater)
            {
                updaters.get(u).update(this);
            }
        }

        Canvas canvas = new Canvas(_g);

        Rect myrect = this.prepareDraw(canvas);
        myrect.left+=point.x;
        myrect.top+=point.y;
        myrect.right+=point.x;
        myrect.bottom+=point.y;
        //this.drawBuffered(canvas,myrect);
        this.draw(canvas,myrect);
        //this.drawBuffered(canvas, myrect);

        return myrect;
    }

    public Rect draw(Graphics _g, Point point)
    {
        return draw(_g, point, null);
    }

    public Rect draw(Graphics _g)
    {
        return draw(_g, offset, null);

        /*
        // inform updaters
        for(int u=0;u<updaters.size();u++)
        {
            updaters.get(u).update();
        }

        Canvas canvas = new Canvas((Graphics2D) _g);
        canvas.setFont(Element.getFont()); //?
        Rect myrect = this.prepareDraw(canvas);
        //this.drawBuffered(canvas,myrect);
        this.draw(canvas,myrect);

        return myrect;/**/
    }

    public Element copy()
    {
            Root ele = new Root(this.getText().copy());
            ele.setComment(this.getComment().copy());
            ele.setColor(this.getColor());
            ele.isNice=this.isNice;
            ele.isProgram=this.isProgram;
            ele.children=(Subqueue) this.children.copy();
            return ele;
    }

    public void addUndo()
    {
            undoList.add(children.copy());
            clearRedo();
    }

    public boolean canUndo()
    {
            return (undoList.size()>0);
    }

    public boolean canRedo()
    {
            return (redoList.size()>0);
    }

    public void clearRedo()
    {
            redoList = new Stack();
    }

    public void clearUndo()
    {
            undoList = new Stack();
    }

    public void undo()
    {
            if (undoList.size()>0)
            {
                    this.hasChanged=true;
                    redoList.add(children.copy());
                    children = (Subqueue) undoList.pop();
                    children.parent=this;
            }
    }

    public void redo()
    {
            if (redoList.size()>0)
            {
                    this.hasChanged=true;
                    undoList.add(children.copy());
                    children = (Subqueue) redoList.pop();
                    children.parent=this;
            }
    }


    public boolean moveDown(Element _ele)
    {
            boolean res = false;
            if(_ele!=null)
            {
                    int i = ((Subqueue) _ele.parent).children.indexOf(_ele);
                    if (!_ele.getClass().getSimpleName().equals("Subqueue") &&
                            !_ele.getClass().getSimpleName().equals("Root") &&
                            ((i+1)<((Subqueue) _ele.parent).children.size()))
                    {
                            ((Subqueue) _ele.parent).children.removeElementAt(i);
                            ((Subqueue) _ele.parent).children.insertElementAt(_ele, i+1);
                            this.hasChanged=true;
                            _ele.setSelected(true);
                            res=true;
                    }
            }
            return res;
    }

    public boolean moveUp(Element _ele)
    {
            boolean res = false;
            if(_ele!=null)
            {
                    int i = ((Subqueue) _ele.parent).children.indexOf(_ele);
                    if (!_ele.getClass().getSimpleName().equals("Subqueue") &&
                            !_ele.getClass().getSimpleName().equals("Root") &&
                            ((i-1>=0)))
                    {
                            ((Subqueue) _ele.parent).removeElement(i);
                            ((Subqueue) _ele.parent).children.insertElementAt(_ele, i-1);
                            this.hasChanged=true;
                            _ele.setSelected(true);
                            res=true;
                    }
            }
            return res;
    }

    public File getFile()
    {
            if(filename.equals(""))
            {
                    return null;
            }
            else
            {
                    return new File(filename);
            }
    }

    public String getPath()
    {
            if (filename.equals(""))
            {
                    return new String();
            }
            else
            {
                    File f = new File(filename);
                    return f.getAbsolutePath();
            }
    }

    /*************************************
     * Extract full text of all Elements
     *************************************/

    private void getFullText(Subqueue _node, StringList _lines)
    {
            if(_node.children.size()>0)
            {
                    for(int i=0;i<_node.children.size();i++)
                    {
                            _lines.add(((Element)_node.children.get(i)).getText());
                            if(_node.children.get(i).getClass().getSimpleName().equals("While"))
                            {
                                    getFullText(((While) _node.children.get(i)).q,_lines);
                            }
                            else if(_node.children.get(i).getClass().getSimpleName().equals("For"))
                            {
                                    getFullText(((For) _node.children.get(i)).q,_lines);
                            }
                            else if(_node.children.get(i).getClass().getSimpleName().equals("Repeat"))
                            {
                                    getFullText(((Repeat) _node.children.get(i)).q,_lines);
                            }
                            else if(_node.children.get(i).getClass().getSimpleName().equals("Alternative"))
                            {
                                    getFullText(((Alternative) _node.children.get(i)).qTrue,_lines);
                                    getFullText(((Alternative) _node.children.get(i)).qFalse,_lines);
                            }
                            else if(_node.children.get(i).getClass().getSimpleName().equals("Case"))
                            {
                                    Case c = ((Case) _node.children.get(i));
                                    for (int j=0;j<c.qs.size();j++)
                                    {
                                            getFullText((Subqueue) c.qs.get(j),_lines);
                                    }
                            }
                    }
            }
    }

    public StringList getFullText()
    {
            StringList sl = text.copy();
            getFullText(children,sl);
            return sl;
    }

    public StringList getFullText(Element _el)
    {
            StringList sl = _el.text.copy();

            if(_el.getClass().getSimpleName().equals("While"))
            {
                    getFullText(((While) _el).q,sl);
            }
            else if(_el.getClass().getSimpleName().equals("For"))
            {
                    getFullText(((For) _el).q,sl);
            }
            else if(_el.getClass().getSimpleName().equals("Repeat"))
            {
                    getFullText(((Repeat) _el).q,sl);
            }
            else if(_el.getClass().getSimpleName().equals("Alternative"))
            {
                    getFullText(((Alternative)_el).qTrue,sl);
                    getFullText(((Alternative) _el).qFalse,sl);
            }
            else if(_el.getClass().getSimpleName().equals("Case"))
            {
                    Case c = ((Case) _el);
                    for (int j=0;j<c.qs.size();j++)
                    {
                            getFullText((Subqueue) c.qs.get(j),sl);
                    }
            }

            return sl;
    }

    /*************************************
     * Extract full text of all Elements
     *************************************/



    private String cleanup(String _s)
    {
            //System.out.println("IN : "+_s);
            if(_s.indexOf("[")>=0)
            {
                    _s=_s.substring(0,_s.indexOf("["));
            }
            if(_s.indexOf(".")>=0)
            {
                    _s=_s.substring(0,_s.indexOf("."));
            }
            //System.out.println("OUT : "+_s);

            return _s;

    }

    //
    // Extract used variable names from an element.
    // HYP 1: (?) <- <used>
    // HYP 2: (?)[<used>] <- <used>
    // HYP 3: [output] <used>
    //
    private StringList getUsedVarNames(Element _ele)
    {
            return getUsedVarNames(_ele,true,false);
    }

    private StringList getUsedVarNames(Element _ele, boolean _includeSelf)
    {
            return getUsedVarNames(_ele,_includeSelf,false);
    }

    public StringList getUsedVarNames(Element _ele, boolean _includeSelf, boolean _onlyMe)
    {
            StringList varNames = new StringList();

            if (_ele!=this)
            {
                    // get body text
                    StringList lines = new StringList();
                    if(_onlyMe==true)
                    {
                            lines.add(_ele.getText());
                    }
                    else
                    {
                            lines = getFullText(_ele);
                            if(_includeSelf==false)
                            {
                                    for(int i=0;i<_ele.getText().count();i++)
                                    {
                                            lines.delete(0);
                                    }
                            }
                    }
                    //System.out.println(lines);
                    /*
                    for(int i=0;i<lines.count();i++)
                    {
                            String allText = lines.get(i);
                            Regex r;

                            // modify "inc" and "dec" function (Pascal)
                            r = new Regex(BString.breakup("inc")+"[(](.*?)[,](.*?)[)](.*?)","$1 <- $1 + $2"); allText=r.replaceAll(allText);
                            r = new Regex(BString.breakup("inc")+"[(](.*?)[)](.*?)","$1 <- $1 + 1"); allText=r.replaceAll(allText);
                            r = new Regex(BString.breakup("dec")+"[(](.*?)[,](.*?)[)](.*?)","$1 <- $1 - $2"); allText=r.replaceAll(allText);
                            r = new Regex(BString.breakup("dec")+"[(](.*?)[)](.*?)","$1 <- $1 - 1"); allText=r.replaceAll(allText);

                            // modify mathematically correct assignments
                            r = new Regex("(.*?)[:][=](.*?)","$1<-$2"); allText=r.replaceAll(allText);     // 1.29

                            //
                            // Should use PARAMETERS HERE!!!
                            //

                            // kick off text parts
                            allText=allText.replaceAll("(.*?)['](.*?)['](.*?)","$1$3");
                            allText=allText.replaceAll("(.*?)[\"](.*?)[\"](.*?)","$1$3");

                            // input
                            r = new Regex(BString.breakup(D7Parser.input.trim())+"[ ](.*?)",D7Parser.input.trim()+" $1"); allText=r.replaceAll(allText);
                            // output
                            r = new Regex(BString.breakup(D7Parser.output.trim())+"[ ](.*?)",D7Parser.output.trim()+" $1"); allText=r.replaceAll(allText);
                            // for
                            r = new Regex(BString.breakup(D7Parser.preFor.trim())+"(.*?)"+D7Parser.postFor.trim()+"(.*?)",D7Parser.preFor.trim()+"$1"+D7Parser.postFor.trim()+"$2"); allText=r.replaceAll(allText);
                            // while
                            r = new Regex(BString.breakup(D7Parser.preWhile.trim())+"(.*?)",D7Parser.preWhile.trim()+"$1"); allText=r.replaceAll(allText);
                            // repeat
                            r = new Regex(BString.breakup(D7Parser.preRepeat.trim())+"(.*?)",D7Parser.preRepeat.trim()); allText=r.replaceAll(allText);
                            // for
                            if(allText.indexOf(D7Parser.preFor.trim())>=0)
                            {
                                    allText=allText.substring(allText.indexOf(D7Parser.preFor.trim())+D7Parser.preFor.trim().length()).trim();
                            }

                            // get names from assignments
                            if(allText.indexOf("<--")>=0)
                            {
                                    int pos = allText.indexOf("<--");

                                    String s = allText.substring(0, pos);
                                    if(allText.indexOf("[")>=0)
                                    {
                                            r = new Regex("(.*?)[\\[](.*?)[\\]](.*?)","$2");
                                            s=r.replaceAll(s);
                                    } else { s=""; }

                                    allText=s+" "+allText.substring(pos+2,allText.length());
                            }
                            if(allText.indexOf("<-")>=0)
                            {
                                    int pos = allText.indexOf("<-");

                                    String s = allText.substring(0, pos);
                                    if(allText.indexOf("[")>=0)
                                    {
                                            r = new Regex("(.*?)[\\[](.*?)[\\]](.*?)","$2");
                                            s=r.replaceAll(s);
                                    } else { s=""; }

                                    allText=s+" "+allText.substring(pos+2,allText.length());
                            }
                            if(allText.indexOf(":=")>=0)
                            {
                                    int pos = allText.indexOf(":=");

                                    String s = allText.substring(0, pos);
                                    if(allText.indexOf("[")>=0)
                                    {
                                            r = new Regex("(.*?)[\\[](.*?)[\\]](.*?)","$2");
                                            s=r.replaceAll(s);
                                    } else { s=""; }

                                    allText=s+" "+allText.substring(pos+2,allText.length());
                            }

                            // cutoff output keyword
                            if(allText.indexOf(D7Parser.output.trim())>=0)
                            {
                                    allText=allText.substring(allText.indexOf(D7Parser.output.trim())+D7Parser.output.trim().length()).trim();
                            }

                            // and constant strings
                            if(allText.indexOf("'")>=0)
                            {

                                    allText=allText.replaceAll("(.*?)['](.*?)['](.*?)","$1$3");
                            }
                            if(allText.indexOf("\"")>=0)
                            {

                                    allText=allText.replaceAll("(.*?)[\"](.*?)[\"](.*?)","$1$3");
                            }

                            // parse out array index
                            if(allText.indexOf(D7Parser.input.trim())>=0)
                            {
                                    if(allText.indexOf("[")>=0)
                                    {
                                            r = new Regex("(.*?)[\\[](.*?)[\\]](.*?)","$2");
                                            allText=r.replaceAll(allText);
                                    }
                                    else
                                    {
                                            allText = "";
                                    }
                            }

                            lines.set(i,allText);
                    }
    				*/
    				
                    // Analyse vor used variables
                    StringList parts = new StringList();
                    parts.add(lines.getLongString());

                    // split
                    parts=StringList.explodeWithDelimiter(parts," ");
                    parts=StringList.explodeWithDelimiter(parts,".");
                    parts=StringList.explodeWithDelimiter(parts,",");
                    parts=StringList.explodeWithDelimiter(parts,"(");
                    parts=StringList.explodeWithDelimiter(parts,")");
                    parts=StringList.explodeWithDelimiter(parts,"[");
                    parts=StringList.explodeWithDelimiter(parts,"]");
                    parts=StringList.explodeWithDelimiter(parts,"-");
                    parts=StringList.explodeWithDelimiter(parts,"+");
                    parts=StringList.explodeWithDelimiter(parts,"/");
                    parts=StringList.explodeWithDelimiter(parts,"*");
                    parts=StringList.explodeWithDelimiter(parts,"mod");
                    parts=StringList.explodeWithDelimiter(parts,"div");
                    parts=StringList.explodeWithDelimiter(parts,">");
                    parts=StringList.explodeWithDelimiter(parts,"<");
                    parts=StringList.explodeWithDelimiter(parts,"=");
                    parts=StringList.explodeWithDelimiter(parts,"!");
                    parts=StringList.explodeWithDelimiter(parts,":");
                    parts=StringList.explodeWithDelimiter(parts,"'");
                    parts=StringList.explodeWithDelimiter(parts,"\"");

                    //reassamble
                    int i = 0;
                    while (i<parts.count())
                    {
                            if(i<parts.count()-2)
                            {
                                    if(parts.get(i).equals("<") && parts.get(i+1).equals("-") && parts.get(i+2).equals("-") )
                                    {
                                            parts.set(i,"<-");
                                            parts.delete(i+1);
                                            parts.delete(i+1);
                                    }
                                    else if(parts.get(i).equals("<") && parts.get(i+1).equals("-"))
                                    {
                                            parts.set(i,"<-");
                                            parts.delete(i+1);
                                    }
                                    else if(parts.get(i).equals(":") && parts.get(i+1).equals("="))
                                    {
                                            parts.set(i,":=");
                                            parts.delete(i+1);
                                    }
                                    else if(parts.get(i).equals("!") && parts.get(i+1).equals("="))
                                    {
                                            parts.set(i,"!=");
                                            parts.delete(i+1);
                                    }
                                    else if(parts.get(i).equals("<") && parts.get(i+1).equals(">"))
                                    {
                                            parts.set(i,"<>");
                                            parts.delete(i+1);
                                    }
                            }
                            else if(i<parts.count()-1)
                            {
                                    if(parts.get(i).equals("<") && parts.get(i+1).equals("-"))
                                    {
                                            parts.set(i,"<-");
                                            parts.delete(i+1);
                                    }
                                    else if(parts.get(i).equals(":") && parts.get(i+1).equals("="))
                                    {
                                            parts.set(i,":=");
                                            parts.delete(i+1);
                                    }
                                    else if(parts.get(i).equals("!") && parts.get(i+1).equals("="))
                                    {
                                            parts.set(i,"!=");
                                            parts.delete(i+1);
                                    }
                                    else if(parts.get(i).equals("<") && parts.get(i+1).equals(">"))
                                    {
                                            parts.set(i,"<>");
                                            parts.delete(i+1);
                                    }
                            }
                            i++;
                    }

                    //this.getVarNames(); // needed?  // CHECKITfile://localhost/Users/robertfisch/Desktop/TEST.nsd

                    for(i=0;i<parts.count();i++)
                    {
                            String display = parts.get(i);

                            display = BString.replace(display, "<--","<-");
                            display = BString.replace(display, "<-","\u2190");

                            if(!display.equals(""))
                            {
                                    if(this.variables.contains(display) && !varNames.contains(display))
                                    {
                                            //System.out.println("Adding: "+display);
                                            varNames.add(display);
                                    }
                            }
                    }

                    /*
                    System.out.println("Lines: "+lines.getCommaText());
                    System.out.println("Parts: "+parts.getCommaText());
                    System.out.println("Vars:  "+variables.getCommaText());
                    System.out.println("Used:  "+varNames.getCommaText());
                    /**/
            }

            varNames=varNames.reverse();
            return varNames;
    }

    // get varnames of a bunch of textlines
    // HYP 1: VARNAME <- (?)
    // HYP 2: [input] VARNAME, VARNAME, VARNAME
    // HYP 3: for VARNAME <- (?) ...
    //
    public StringList getVarnames(StringList lines)
    {
            StringList varNames = new StringList();
            /*
            for(int i=0;i<lines.count();i++)
            {
                    String allText = lines.get(i);
                    Regex r;

                    // modify "inc" and "dec" function (Pascal)
                    r = new Regex(BString.breakup("inc")+"[(](.*?)[,](.*?)[)](.*?)","$1 <- $1 + $2"); allText=r.replaceAll(allText);
                    r = new Regex(BString.breakup("inc")+"[(](.*?)[)](.*?)","$1 <- $1 + 1"); allText=r.replaceAll(allText);
                    r = new Regex(BString.breakup("dec")+"[(](.*?)[,](.*?)[)](.*?)","$1 <- $1 - $2"); allText=r.replaceAll(allText);
                    r = new Regex(BString.breakup("dec")+"[(](.*?)[)](.*?)","$1 <- $1 - 1"); allText=r.replaceAll(allText);

                    // modify mathematically correct assignments
                    r = new Regex("(.*?)[:][=](.*?)","$1<-$2"); allText=r.replaceAll(allText);     // 1.29

                    //
                    // Should use PARAMETERS HERE!!!
                    //

                    // input
                    r = new Regex(BString.breakup(D7Parser.input.trim())+"[ ](.*?)",D7Parser.input.trim()+" $1"); allText=r.replaceAll(allText);
                    // output
                    r = new Regex(BString.breakup(D7Parser.output.trim())+"[ ](.*?)",D7Parser.output.trim()+" $1"); allText=r.replaceAll(allText);
                    // for
                    r = new Regex(BString.breakup(D7Parser.preFor.trim())+"(.*?)"+D7Parser.postFor.trim()+"(.*?)",D7Parser.preFor.trim()+"$1"+D7Parser.postFor.trim()+"$2"); allText=r.replaceAll(allText);
                    // while
                    r = new Regex(BString.breakup(D7Parser.preWhile.trim())+"(.*?)",D7Parser.preWhile.trim()+"$1"); allText=r.replaceAll(allText);
                    // repeat
                    r = new Regex(BString.breakup(D7Parser.preRepeat.trim())+"(.*?)",D7Parser.preRepeat.trim()); allText=r.replaceAll(allText);
                    // for
                    if(allText.indexOf(D7Parser.preFor.trim())>=0)
                    {
                            allText=allText.substring(allText.indexOf(D7Parser.preFor.trim())+D7Parser.preFor.trim().length()).trim();
                    }

                    // get names from assignments
                    if(allText.indexOf("<--")>=0)
                    {
                            int pos = allText.indexOf("<--");
                        allText=allText.substring(0,pos);
                            varNames.addOrderedIfNew(cleanup(allText.trim()));
                    }
                    if(allText.indexOf("<-")>=0)
                    {
                            int pos = allText.indexOf("<-");
                        allText=allText.substring(0,pos);
                            varNames.addOrderedIfNew(cleanup(allText.trim()));
                    }
                    if(allText.indexOf(":=")>=0)
                    {
                            int pos = allText.indexOf(":=");
                        allText=allText.substring(0,pos);
                            varNames.addOrderedIfNew(cleanup(allText.trim()));
                    }

                    // get names from read statements
                    if(allText.indexOf(D7Parser.input.trim())>=0)
                    {
                            String sr=allText.substring(allText.indexOf(D7Parser.input.trim())+D7Parser.input.trim().length()).trim();
                            StringList str = StringList.explode(sr,",");
                            if(str.count()>0)
                            {
                                    for(int j=0;j<str.count();j++)
                                    {
                                            String s = str.get(j);
                                            r = new Regex("(.*?)[\\[](.*?)[\\]](.*?)","$1$3");
                                            s=r.replaceAll(s);

                                            if(!s.trim().equals(""))
                                            {
                                                    varNames.addOrderedIfNew(s.trim());
                                            }
                                    }
                            }
                    }


                    lines.set(i,allText);
            }
			*/
            return varNames;
    }


    //
    // Extract all the variable names of the entire program.
    //
    public StringList getVarNames()
    {
            StringList varNames = new StringList();

            // check root text for variable names
            // !!
            // !! This works only for Pascal-like syntax: functionname (<name>, <name>, ..., <name>:<type>; ...)
            // !! or VBA like syntax: functionname(<name>, <name> as <type>; ...)
            // !!
            try
            {
                    if(this.isProgram==false)
                    {
                            String rootText = this.getText().getText();
                            rootText = rootText.replace("var ", "");
                            if(rootText.indexOf("(")>=0)
                            {
                                    rootText=rootText.substring(rootText.indexOf("(")+1).trim();
                                    rootText=rootText.substring(0,rootText.indexOf(")")).trim();
                            }

                            StringList params = StringList.explode(rootText,";");
                            if(params.count()>0)
                            {
                                    for(int i=0;i<params.count();i++)
                                    {
                                            String S = params.get(i);
                                            if(S.indexOf(":")>=0)
                                            {
                                                    S=S.substring(0,S.indexOf(":")).trim();
                                            }
                                            if(S.indexOf("as")>=0)
                                            {
                                                    S=S.substring(0,S.indexOf("as")).trim();
                                            }
                                            StringList vars = StringList.explode(S,",");
                                            for(int j=0;j<vars.count();j++)
                                            {
                                                    if(!vars.get(j).trim().equals(""))
                                                    {
                                                            varNames.add(vars.get(j).trim());
                                                    }
                                            }
                                    }
                            }
                    }
            }
            catch (Exception e)
            {
                    // do nothing
            }

            // get body text
            StringList lines = getFullText();
            
            if(!(this instanceof Root)) varNames.add(getVarnames(lines));

            varNames=varNames.reverse();
            this.variables=varNames;
            //System.out.println(varNames.getCommaText());
            return varNames;
    }

    //
    // Extract all the variable names of the selected element.
    //
    public StringList getVarNames(Element _ele)
    {
            return getVarNames(_ele, true, false);
    }

    public StringList getVarNames(Element _ele, boolean _onlyMe)
    {
            return getVarNames(_ele, _onlyMe, false);
    }

    public StringList getVarNames(Element _ele, boolean _onlyMe, boolean _onlyBody)
    {

            StringList varNames = new StringList();

            // check root text for variable names
            // !!
            // !! This works only for Pascal-like syntax: functionname (<name>, <name>, ..., <name>:<type>; ...)
            // !! or VBA like syntax: functionname(<name>, <name> as <type>; ...)
            // !!
            try
            {
                    if(this.isProgram==false && _ele==this)
                    {
                            String rootText = this.getText().getText();
                            rootText = rootText.replace("var ", "");
                            if(rootText.indexOf("(")>=0)
                            {
                                    rootText=rootText.substring(rootText.indexOf("(")+1).trim();
                                    rootText=rootText.substring(0,rootText.indexOf(")")).trim();
                            }

                            StringList params = StringList.explode(rootText,";");
                            if(params.count()>0)
                            {
                                    for(int i=0;i<params.count();i++)
                                    {
                                            String S = params.get(i);
                                            if(S.indexOf(":")>=0)
                                            {
                                                    S=S.substring(0,S.indexOf(":")).trim();
                                            }
                                            if(S.indexOf("as")>=0)
                                            {
                                                    S=S.substring(0,S.indexOf("as")).trim();
                                            }
                                            StringList vars = StringList.explode(S,",");
                                            for(int j=0;j<vars.count();j++)
                                            {
                                                    if(!vars.get(j).trim().equals(""))
                                                    {
                                                            varNames.add(vars.get(j).trim());
                                                    }
                                            }
                                    }
                            }
                    }
            }
            catch (Exception e)
            {
                    System.out.println(e.getMessage());
            }

            // get body text
            StringList lines;
            if(_onlyMe==true)
            {
                    lines = _ele.text.copy();
            }
            else
            {
                    lines = getFullText(_ele);
            }

            if(_onlyBody==true)
            {
                    for(int l=0;l<_ele.text.count();l++)
                    {
                            lines.delete(0);
                    }
            }

            if (!(this instanceof Root)) varNames.add(getVarnames(lines));

            varNames=varNames.reverse();
            //this.variables=varNames;
            //System.out.println(varNames.getCommaText());
            return varNames;
    }


    private void analyse(Subqueue _node, Vector _errors, StringList _vars, StringList _uncertainVars)
    {
            DetectedError error;
            /*
            if(_node.children.size()>0)
            {
                    for(int i=0;i<_node.children.size();i++)
                    {
                            // get var from actual instruction
                            StringList myVars = getVarNames((Element) _node.children.get(i));


                            // CHECK: assignment in condition (#8)
                            if(_node.children.get(i).getClass().getSimpleName().equals("While")
                               ||
                               _node.children.get(i).getClass().getSimpleName().equals("Repeat")
                               ||
                               _node.children.get(i).getClass().getSimpleName().equals("Alternative"))
                            {
                                    String text = ((Element) _node.children.get(i)).getText().getLongString();
                                    if ( text.contains("<-") || text.contains(":=") || text.contains("<--"))
                                    {
                                            //error  = new DetectedError("It is not allowed to make an assignment inside a condition.",(Element) _node.children.get(i));
                                    	//error  = new DetectedError(errorMsg(Menu.error08,""),(Element) _node.children.get(i));
                                    	//addError(_errors,error,8);
                                    }
                            }


                            // CHECK: two checks in one loop: (#5) & (#7)
                            for(int j=0;j<myVars.count();j++)
                            {
                                    // CHECK: non uppercase var (#5)
                                    if(!myVars.get(j).toUpperCase().equals(myVars.get(j)) && !rootVars.contains(myVars.get(j)))
                                    {
                                            //error  = new DetectedError("The variable ��"+myVars.get(j)+"�� must be written in uppercase!",(Element) _node.children.get(i));
                                            if(!((myVars.get(j).toLowerCase().equals("result") && this.isProgram==false)))
                                            {
                                            	//error  = new DetectedError(errorMsg(Menu.error05,myVars.get(j)),(Element) _node.children.get(i));
                                            	//addError(_errors,error,5);
                                            }
                                    }

                                    // CHECK: correkt identifiers (#7)
                                    if(testidentifier(myVars.get(j))==false)
                                    {
                                            //error  = new DetectedError("��"+myVars.get(j)+"�� is not a valid name for a variable!",(Element) _node.children.get(i));
                                    	//error  = new DetectedError(errorMsg(Menu.error07_3,myVars.get(j)),(Element) _node.children.get(i));
                                    	//addError(_errors,error,7);
                                    }
                            }

                            // CHECK: two checks with the same input condition
                            if(_node.children.get(i).getClass().getSimpleName().equals("Instruction"))
                            {
                                    StringList test = ((Element) _node.children.get(i)).getText();

                                    // CHECK: wrong multi-line instruction (#10 - new!)
                                    int isInput = 0;
                                    int isOutput = 0;
                                    int isAssignment = 0;

                                    for(int l=0;l<test.count();l++)
                                    {
                                            // CHECK: wrong affection (#11 - new!)
                                            String myTest = test.get(l);
                            

                                            myTest=myTest.replaceAll("(.*?)['](.*?)['](.*?)","$1$3");
                                            myTest=myTest.replaceAll("(.*?)[\"](.*?)[\"](.*?)","$1$3");

                                            //System.out.println(" -- "+myTest);

                                            if((myTest.contains("=") || myTest.contains("==")) && !myTest.contains("<--") && !myTest.contains("<-") && !myTest.contains(":="))
                                            {
                                                    //error  = new DetectedError("You probably made an assignment error. Please check this instruction!",(Element) _node.children.get(i));
                                                    error  = new DetectedError(errorMsg(Menu.error11,""),(Element) _node.children.get(i));
                                                    addError(_errors,error,11);
                                            }

                                            // CHECK: wrong multi-line instruction (#10 - new!)
                                            String myText = test.get(l);
                                            if (myText.contains(D7Parser.input.trim())) {isInput=1;}
                                            if (myText.contains(D7Parser.output.trim())) {isOutput=1;}
                                            if ( myText.contains("<-") || myText.contains(":=") || myText.contains("<--")) {isAssignment=1;}
                                    }
                                    // CHECK: wrong multi-line instruction (#10 - new!)
                                    if (isInput+isOutput+isAssignment==3)
                                    {
                                            //error  = new DetectedError("A single instruction element should not contain input/output instructions and assignments!",(Element) _node.children.get(i));
                                            error  = new DetectedError(errorMsg(Menu.error10_1,""),(Element) _node.children.get(i));
                                            addError(_errors,error,10);
                                    }
                                    else if (isInput+isOutput==2)
                                    {
                                            //error  = new DetectedError("A single instruction element should not contain input and output instructions!",(Element) _node.children.get(i));
                                            error  = new DetectedError(errorMsg(Menu.error10_2,""),(Element) _node.children.get(i));
                                            addError(_errors,error,10);
                                    }
                                    else if (isInput+isAssignment==2)
                                    {
                                            //error  = new DetectedError("A single instruction element should not contain input instructions and assignments!",(Element) _node.children.get(i));
                                            error  = new DetectedError(errorMsg(Menu.error10_3,""),(Element) _node.children.get(i));
                                            addError(_errors,error,10);
                                    }
                                    else if (isOutput+isAssignment==2)
                                    {
                                            //error  = new DetectedError("A single instruction element should not contain ouput instructions and assignments!",(Element) _node.children.get(i));
                                            error  = new DetectedError(errorMsg(Menu.error10_4,""),(Element) _node.children.get(i));
                                            addError(_errors,error,10);
                                    }
                            }


                            // CHECK: non init var (no REPEAT)  (#3)
                            StringList myUsed = getUsedVarNames((Element) _node.children.get(i),true,true);
                            if(!_node.children.get(i).getClass().getSimpleName().equals("Repeat"))
                            {
                                    for(int j=0;j<myUsed.count();j++)
                                    {
                                            if(!_vars.contains(myUsed.get(j)) && !_uncertainVars.contains(myUsed.get(j)))
                                            {
                                                    //error  = new DetectedError("The variable ��"+myUsed.get(j)+"�� has not yet been initialized!",(Element) _node.children.get(i));
                                                    error  = new DetectedError(errorMsg(Menu.error03_1,myUsed.get(j)),(Element) _node.children.get(i));
                                                    addError(_errors,error,3);
                                            }
                                            else if(_uncertainVars.contains(myUsed.get(j)))
                                            {
                                                    //error  = new DetectedError("The variable ��"+myUsed.get(j)+"�� may not have been initialized!",(Element) _node.children.get(i));
                                                    error  = new DetectedError(errorMsg(Menu.error03_2,myUsed.get(j)),(Element) _node.children.get(i));
                                                    addError(_errors,error,3);
                                            }
                                    }
                            }



            StringList sl =((Element) _node.children.get(i)).getText();
            for(int ls=0;ls<sl.count();ls++)
            {
                if(sl.get(ls).trim().toLowerCase().indexOf("return")==0)
                {
                    myVars.addIfNew("result");
                }
            }

                            // add detected var to initialised vars
                            _vars.addIfNew(myVars);


                            // CHECK: endless loop (#2)
                            if(_node.children.get(i).getClass().getSimpleName().equals("While")
                               ||
                               _node.children.get(i).getClass().getSimpleName().equals("Repeat"))
                            {
                                    // get used variable from inside the loop
                                    StringList usedVars = getVarNames((Element) _node.children.get(i),false);
                                    // get loop variables
                                    StringList loopVars = getUsedVarNames((Element) _node.children.get(i),true,true);


                                    boolean check = false;
                                    for(int j=0;j<loopVars.count();j++)
                                    {
                                            check = check || usedVars.contains(loopVars.get(j));
                                    }
                                    if (check==false)
                                    {
                                            //error  = new DetectedError("No change of the variables in the condition detected. Possible endless loop ...",(Element) _node.children.get(i));
                                            error  = new DetectedError(errorMsg(Menu.error02,""),(Element) _node.children.get(i));
                                            addError(_errors,error,2);
                                    }
                            }

                            // CHECK: loop var modified (#1)
                            if(_node.children.get(i).getClass().getSimpleName().equals("For"))
                            {
                                    // get used variable from inside the FOR-loop
                                    StringList usedVars = getVarNames((Element) _node.children.get(i),false,true);
                                    // get loop variable (that should be only one!!!)
                                    StringList loopVars = getVarNames((Element) _node.children.get(i),true);

       

                                    if(loopVars.count()==0)
                                    {
                                            //error  = new DetectedError("WARNING: No loop variable detected ...",(Element) _node.children.get(i));
                                            error  = new DetectedError(errorMsg(Menu.error01_1,""),(Element) _node.children.get(i));
                                            addError(_errors,error,1);
                                    }
                                    else
                                    {
                                            if(loopVars.count()>1)
                                            {
                                                    //error  = new DetectedError("WARNING: More than one loop variable detected ...",(Element) _node.children.get(i));
                                                    error  = new DetectedError(errorMsg(Menu.error01_2,""),(Element) _node.children.get(i));
                                                    addError(_errors,error,1);
                                            }

                                            if(usedVars.contains(loopVars.get(0)))
                                            {
                                               //error  = new DetectedError("You are not allowed to modify the loop variable ��"+loopVars.get(0)+"�� inside the loop!",(Element) _node.children.get(i));
                                               error  = new DetectedError(errorMsg(Menu.error01_3,loopVars.get(0)),(Element) _node.children.get(i));
                                               addError(_errors,error,1);
                                            }
                                    }
                            }

                            // CHECK: if with empty T-block (#4)
                            if(_node.children.get(i).getClass().getSimpleName().equals("Alternative"))
                            {
                                    if(((Alternative) _node.children.get(i)).qTrue.children.size()==0)
                                    {
                                            //error  = new DetectedError("You are not allowed to use an IF-statement with an empty TRUE-block!",(Element) _node.children.get(i));
                                            error  = new DetectedError(errorMsg(Menu.error04,""),(Element) _node.children.get(i));
                                            addError(_errors,error,4);
                                    }
                            }

                            // continue analysis for subelements
                            if(_node.children.get(i).getClass().getSimpleName().equals("While"))
                            {
                                    analyse(((While) _node.children.get(i)).q,_errors,_vars,_uncertainVars);
                            }
                            else if(_node.children.get(i).getClass().getSimpleName().equals("For"))
                            {
                                    analyse(((For) _node.children.get(i)).q,_errors,_vars,_uncertainVars);
                            }
                            else if(_node.children.get(i).getClass().getSimpleName().equals("Repeat"))
                            {
                                    analyse(((Repeat) _node.children.get(i)).q,_errors,_vars,_uncertainVars);

                                    // CHECK: non init var (REPEAT only, because it must be analysed _after_ the body!)  (#3)
                                 

                                    //myUsed = getUsedVarNames((Element) _node.children.get(i),true,true);
                                    for(int j=0;j<myUsed.count();j++)
                                    {
                                            if(!_vars.contains(myUsed.get(j)) && !_uncertainVars.contains(myUsed.get(j)))
                                            {
                                                    //error  = new DetectedError("The variable ��"+myUsed.get(j)+"�� has not yet been initialized!",(Element) _node.children.get(i));
                                                    error  = new DetectedError(errorMsg(Menu.error03_1,myUsed.get(j)),(Element) _node.children.get(i));
                                                    addError(_errors,error,3);
                                            }
                                            else if(_uncertainVars.contains(myUsed.get(j)))
                                            {
                                                    //error  = new DetectedError("The variable ��"+myUsed.get(j)+"�� may not have been initialized!",(Element) _node.children.get(i));
                                                    error  = new DetectedError(errorMsg(Menu.error03_2,myUsed.get(j)),(Element) _node.children.get(i));
                                                    addError(_errors,error,3);
                                            }
                                    }

                            }
                            else if(_node.children.get(i).getClass().getSimpleName().equals("Alternative"))
                            {
                                    StringList tVars = _vars.copy();
                                    StringList fVars = _vars.copy();

                                    analyse(((Alternative) _node.children.get(i)).qTrue,_errors,tVars,_uncertainVars);
                                    analyse(((Alternative) _node.children.get(i)).qFalse,_errors,fVars,_uncertainVars);

                                    for(int v=0;v<tVars.count();v++)
                                    {
                                            if(fVars.contains(tVars.get(v))) {_vars.addIfNew(tVars.get(v)); }
                                            else if(!_vars.contains(tVars.get(v))) {_uncertainVars.add(tVars.get(v));}
                                    }
                                    for(int v=0;v<fVars.count();v++)
                                    {
                                            if(tVars.contains(fVars.get(v))) {_vars.addIfNew(fVars.get(v)); }
                                            else if(!_vars.contains(fVars.get(v))) {_uncertainVars.addIfNew(fVars.get(v));}
                                    }

                                    // if a variable is not being initialised on both of the lists,
                                    // it could be considered ass not being always initialised
                                    //
                                    // => use a second list with variable that "may not have been initialised"
                            }
                            else if(_node.children.get(i).getClass().getSimpleName().equals("Case"))
                            {
                                    Case c = ((Case) _node.children.get(i));
                                    StringList initialVars = _vars.copy();
                                    Hashtable myInitVars = new Hashtable();
                                    for (int j=0;j<c.qs.size();j++)
                                    {
                                            StringList caseVars = initialVars.copy();
                                            analyse((Subqueue) c.qs.get(j),_errors,caseVars,_uncertainVars);
                                            for(int v = 0;v<caseVars.count();v++)
                                            {
                                                    if(myInitVars.containsKey(caseVars.get(v)))
                                                    {
                                                            myInitVars.put(caseVars.get(v), ((String) myInitVars.get(caseVars.get(v)))+"1");
                                                    }
                                                    else
                                                    {
                                                            myInitVars.put(caseVars.get(v), "1");
                                                    }
                                            }
                                            //_vars.addIfNew(caseVars);
                                    }
                                    //System.out.println(myInitVars);
                                    // walk trought the hashtable and check
                                    Enumeration keys = myInitVars.keys();
                                    while ( keys.hasMoreElements() )
                                    {
                                            String key = (String) keys.nextElement();
                                            String value = (String) myInitVars.get(key);

                                            int si = c.qs.size();
                                            // adapt size if no "default"
                                            if(((String)c.text.get(c.text.count()-1)).equals("%"))
                                            {
                                                    si-=1;
                                            }
                                            //System.out.println("SI = "+si+" = "+c.text.get(c.text.count()-1));

                                            if(value.length()==si)
                                            {
                                                    _vars.addIfNew(key);
                                            }
                                            else
                                            {
                                                    if(!_vars.contains(key))
                                                    {
                                                            _uncertainVars.addIfNew(key);
                                                    }
                                            }
                                    }
                                    // look at the comment for the IF-structure
                            }
                    }
            }
            */
    }

    private boolean testidentifier(String _str)
    {
            boolean result = true;
            _str=_str.trim();
            if(_str.equals(""))
            {
                    result=false;
            }
            else
            {
                    if(
                       ('a'<=_str.toLowerCase().charAt(0) && _str.toLowerCase().charAt(0)<='z')
                       ||
                       (_str.toLowerCase().charAt(0)=='_')
                      )
                    {
                            if (_str.length()>1)
                            {
                                    for(int i=0;i<_str.length();i++)
                                    {
                                            if(!(
                                               ('a'<=_str.toLowerCase().charAt(0) && _str.toLowerCase().charAt(0)<='z')
                                               ||
                                               ('0'<=_str.charAt(0) && _str.charAt(0)<='9')
                                               ||
                                               (_str.toLowerCase().charAt(0)=='_')
                                               ))
                                            {
                                                    result = false;
                                            }
                                    }
                            }
                    }
                    else
                    {
                            result = false;
                    }

            }
            return result;
    }

    private void addError(Vector errors, DetectedError error, int errorNo)
    {
            switch (errorNo)
            {
                    case 1:
                            if (Root.check1) errors.add(error);
                            break;
                    case 2:
                            if (Root.check2) errors.add(error);
                            break;
                    case 3:
                            if (Root.check3) errors.add(error);
                            break;
                    case 4:
                            if (Root.check4) errors.add(error);
                            break;
                    case 5:
                            if (Root.check5) errors.add(error);
                            break;
                    case 6:
                            if (Root.check6) errors.add(error);
                            break;
                    case 7:
                            if (Root.check7) errors.add(error);
                            break;
                    case 8:
                            if (Root.check8) errors.add(error);
                            break;
                    case 9:
                            if (Root.check9) errors.add(error);
                            break;
                    case 10:
                            if (Root.check10) errors.add(error);
                            break;
                    case 11:
                            if (Root.check11) errors.add(error);
                            break;
                    case 12:
                            if (Root.check12) errors.add(error);
                            break;
                    case 13:
                            if (Root.check13) errors.add(error);
                            break;
                    default:
                            errors.add(error);
                            break;
            }
    }

public StringList getParameterNames()
{
    StringList test = this.getVarNames();
    System.out.println(test);
    StringList vars = getVarNames(this,true,false);
    System.out.println(vars);
    return vars;
}

public String getMethodName()
{
            String rootText = text.getLongString();
            int pos;

            pos = rootText.indexOf("(");
            if (pos!=-1) rootText=rootText.substring(0,pos);
            pos = rootText.indexOf("[");
            if (pos!=-1) rootText=rootText.substring(0,pos);
            pos = rootText.indexOf(":");
            if (pos!=-1) rootText=rootText.substring(0,pos);

            String programName = rootText.trim();

    return programName;
}

    public Vector analyse()
    {
           /*
    		this.getVarNames();

            Vector errors = new Vector();
            StringList vars = getVarNames(this,true,false);
            rootVars = getVarNames(this,true,false);
            StringList uncertainVars = new StringList();

            String rootText = text.getLongString();
            Regex r;
            int pos;

            pos = rootText.indexOf("(");
            if (pos!=-1) rootText=rootText.substring(0,pos);
            pos = rootText.indexOf("[");
            if (pos!=-1) rootText=rootText.substring(0,pos);
            pos = rootText.indexOf(":");
            if (pos!=-1) rootText=rootText.substring(0,pos);

            String programName = rootText.trim();

            DetectedError error;

            // CHECK: uppercase for programname (#6)
            if(!programName.toUpperCase().equals(programName))
            {
                    //error  = new DetectedError("The programname ��"+programName+"�� must be written in uppercase!",this);
                    error  = new DetectedError(errorMsg(Menu.error06,programName),this);
                    addError(errors,error,6);
            }

            // CHECK: correct identifier for programname (#7)
            if(testidentifier(programName)==false)
            {
                    //error  = new DetectedError("��"+programName+"�� is not a valid name for a program or function!",this);
                    error  = new DetectedError(errorMsg(Menu.error07_1,programName),this);
                    addError(errors,error,7);
            }

            // CHECK: two checks in one loop: (#12 - new!) & (#7)
            for(int j=0;j<vars.count();j++)
            {
                    // CHECK: non conform parameter name (#12 - new!)
                    if( !(vars.get(j).charAt(0)=='p' && vars.get(j).substring(1).toUpperCase().equals(vars.get(j).substring(1))) )
                    {
                            //error  = new DetectedError("The parameter ��"+vars.get(j)+"�� must start with the letter \"p\" followed by only uppercase letters!",this);
                            error  = new DetectedError(errorMsg(Menu.error12,vars.get(j)),this);
                            addError(errors,error,12);
                    }

                    // CHECK: correkt identifiers (#7)
                    if(testidentifier(vars.get(j))==false)
                    {
                            //error  = new DetectedError("��"+vars.get(j)+"�� is not a valid name for a parameter!",this);
                            error  = new DetectedError(errorMsg(Menu.error07_2,vars.get(j)),this);
                            addError(errors,error,7);
                    }
            }


            // CHECK: the content of the diagram
            analyse(this.children,errors,vars,uncertainVars);

            // Test if we have a function (return value) or not
            String first = this.getText().get(0).trim();
            boolean haveFunction = first.contains(") as") || first.contains(") AS")  || first.contains(") As") || first.contains(") aS") || first.contains(") :") || first.contains("):");

            // CHECK: var = programname (#9)
            if(variables.contains(programName) && haveFunction==false)
            {
                    //error  = new DetectedError("Your program (��"+programName+"��) cannot have the same name as a variable!",this);
                    error  = new DetectedError(errorMsg(Menu.error09,programName),this);
                    addError(errors,error,9);
            }

            // CHECK: sub does not return any result (#13 - new!)
            // pre-requirement: we have a sub that return something ...  FUNCTIONNAME () <return type>
            // check to see if the name of the sub (proposed filename)
            // is contained in the name of he assigned variablename
            // _ OR _
            // the list of initialized variables contains "RESULT"
            if(haveFunction==true)
            {
                    if (!vars.contains("result",false) && !vars.contains(programName,false)
                        &&
                            !uncertainVars.contains("result",false) && !uncertainVars.contains(programName,false)
                       )
                    {
                            //error  = new DetectedError("Your function does not return any result!",this);
                            error  = new DetectedError(errorMsg(Menu.error13_1,""),this);
                            addError(errors,error,13);
                    }
                    else if (
                                     (!vars.contains("result",false) && !vars.contains(programName,false))
                                     &&
                                     (uncertainVars.contains("result",false) || uncertainVars.contains(programName,false))
                                     )
                    {
                            //error  = new DetectedError("Your function may not return a result!",this);
                            error  = new DetectedError(errorMsg(Menu.error13_2,""),this);
                            addError(errors,error,13);
                    }
            }

     

            this.errors=errors;
            return errors;
            */
    	return new Vector();
    }

    
}
