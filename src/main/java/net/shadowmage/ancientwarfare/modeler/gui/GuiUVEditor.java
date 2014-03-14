package net.shadowmage.ancientwarfare.modeler.gui;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.GuiElement;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.NumberInput;
import net.shadowmage.ancientwarfare.core.gui.elements.TexturedRectangleLive;
import net.shadowmage.ancientwarfare.core.model.ModelPiece;
import net.shadowmage.ancientwarfare.core.model.Primitive;
import net.shadowmage.ancientwarfare.core.model.PrimitiveBox;
import net.shadowmage.ancientwarfare.core.model.PrimitiveQuad;
import net.shadowmage.ancientwarfare.core.model.PrimitiveTriangle;
import net.shadowmage.ancientwarfare.core.util.AWTextureManager;

/**
 * UV editor for MEIM
 * @author Shadowmage
 *
 */
public class GuiUVEditor extends GuiContainerBase
{

GuiModelEditor parent;

TexturedRectangleLive textureRect;

CompositeScrolled primitiveControlArea;
CompositeScrolled pieceListArea;
CompositeScrolled fileControlArea;
CompositeScrolled textureControlArea;//set texture x/y size
Label pieceNameLabel;
Label primitiveNameLabel;

static int textureXSize = 256;
static int textureYSize = 256;

BufferedImage image;

//map of label-element combos, to select pieces through clicking on/in the piece list area
private HashMap<Label, ModelPiece> pieceMap = new HashMap<Label, ModelPiece>();
private HashMap<Label, Primitive> primitiveMap = new HashMap<Label, Primitive>();

public GuiUVEditor(GuiModelEditor parent)
  {
  super((ContainerBase) parent.inventorySlots, 256, 256, defaultBackground);
  this.parent = parent;
  image = AWTextureManager.instance().getTexture("editorTexture").getImage();
  }

@Override
public void initElements()
  {
  textureRect = new TexturedRectangleLive(0, 0, xSize, ySize, textureXSize, textureYSize, 0, 0, textureXSize, textureYSize, "editorTexture");
  this.addGuiElement(textureRect);
  
  textureControlArea = new CompositeScrolled(-guiLeft, -guiTop, (width - xSize)/2, height/2);
  addGuiElement(textureControlArea);
  
  primitiveControlArea = new CompositeScrolled(-guiLeft, -guiTop + height/2, (width - xSize)/2, height/2);
  addGuiElement(primitiveControlArea);
  
  fileControlArea = new CompositeScrolled(width, -guiTop, (width - xSize)/2, height/2);
  addGuiElement(fileControlArea);
  
  pieceListArea = new CompositeScrolled(width, -guiTop + height/2, (width - xSize)/2, height/2);
  addGuiElement(pieceListArea);
  
  pieceNameLabel = new Label(8, -guiTop, "Piece: No Selection");
  addGuiElement(pieceNameLabel);
  
  primitiveNameLabel = new Label(8, -guiTop + 10, "Primitive: No Selection");
  addGuiElement(primitiveNameLabel);
  }

@Override
public void setupElements()
  {
  textureControlArea.setRenderPosition(-guiLeft, -guiTop);//top-left
  primitiveControlArea.setRenderPosition(-guiLeft, -guiTop + height/2);//bottom-left
  fileControlArea.setRenderPosition(xSize, -guiTop);//top-right
  pieceListArea.setRenderPosition(xSize, -guiTop + height/2);//bottom-right
  
  pieceNameLabel.setRenderPosition(8, -guiTop);
  primitiveNameLabel.setRenderPosition(8, -guiTop+10);
  
  pieceNameLabel.setText(parent.getModelPiece()==null? "Piece: No Selection" : "Piece: "+parent.getModelPiece().getName());
  primitiveNameLabel.setText(parent.getPrimitive()==null? "Primitive: No Selection" : "Primitive: "+parent.getPrimitive().toString());
  
  primitiveControlArea.clearElements();
  pieceListArea.clearElements();
  textureControlArea.clearElements();
//fileControlArea.clearElements();
  
  primitiveMap.clear();
  pieceMap.clear();
  
  this.removeGuiElement(textureRect);
  textureRect = new TexturedRectangleLive(0, 0, xSize, ySize, textureXSize, textureYSize, 0, 0, textureXSize, textureYSize, "editorTexture");
  this.addGuiElement(textureRect);
  
  addPieceList();
  
  addTextureControls();
  
  addPrimitiveControls();
  
  addFileControls();
  
  }

private void setTextureXSize(int size)
  {
  textureXSize = size;
  updateTextureSize();
  }

private void setTextureYSize(int size)
  {
  textureYSize = size;
  updateTextureSize();
  }

/**
 * builds a new buffered image and texture-rendering widget with the currently set texture size
 * subsequently calls updateTexture() to upload the new image to gfx texture
 */
private void updateTextureSize()
  {  
  this.image = new BufferedImage(textureXSize, textureYSize, BufferedImage.TYPE_INT_ARGB);
  updateTexture();
  refreshGui();
  }

/**
 * should be called whenever a piece moves and the texture needs updating;
 */
private void updateTexture()
  {
  for(int x = 0; x< image.getWidth(); x++)
    {
    for(int y = 0; y< image.getHeight(); y++)
      {
      image.setRGB(x, y, 0x00ffffff);//clear image to default  white 100% alpha (transparent)
      }
    }
  ArrayList<ModelPiece> pieces = new ArrayList<ModelPiece>();
  parent.model.getPieces(pieces);
  for(ModelPiece piece : pieces)
    {
    for(Primitive primitive : piece.getPrimitives())
      {
      primitive.addUVMapToImage(image);
      }
    }
  AWTextureManager.instance().updateTextureContents("editorTexture", image);
  }

private void addTextureControls()
  {
  int w = ((width - xSize)/2)-17;
  int c0 = 5;//label
  int c1 = c0+17;//-
  int c2 = c1+12;//20+12 --input
  int c3 = 2 + w - 12;//+      
  int w2 = w - 24 - 20;//width of the input bar
  int totalHeight = 3;  
  
  Label label;
  NumberInput input;
  Button button;
  
  label = new Label(c0, totalHeight, StatCollector.translateToLocal("guistrings.texture_size")+":");
  textureControlArea.addGuiElement(label);
  totalHeight+=12;
  
  /***************************************** X SIZE **********************************************/
  label = new Label(c0, totalHeight, "X:");
  textureControlArea.addGuiElement(label);
  
  input = new NumberInput(c2, totalHeight, w2, textureXSize, this)
    {
    @Override
    public void onValueUpdated(float value)
      {
      setTextureXSize((int)value);
      }
    };
  input.setIntegerValue();
  textureControlArea.addGuiElement(input);
  
  button = new Button(c1, totalHeight, 12, 12, "-")
    {
    @Override
    protected void onPressed()
      {
      if(textureXSize>1)
        {
        setTextureXSize(textureXSize-1);
        }
      }
    };
  textureControlArea.addGuiElement(button);
  
  button = new Button(c3, totalHeight, 12, 12, "+")
    {
    @Override
    protected void onPressed()
      {
      if(textureXSize<1024)
        {
        setTextureXSize(textureXSize+1);
        }
      }
    };
  textureControlArea.addGuiElement(button);
  
  totalHeight+=12;
  
  /***************************************** Y SIZE **********************************************/
  label = new Label(c0, totalHeight, "Y:");
  textureControlArea.addGuiElement(label);
  
  input = new NumberInput(c2, totalHeight, w2, textureYSize, this)
    {
    @Override
    public void onValueUpdated(float value)
      {
      setTextureYSize((int)value);
      }
    };
  input.setIntegerValue();
  textureControlArea.addGuiElement(input);
  
  button = new Button(c1, totalHeight, 12, 12, "-")
    {
    @Override
    protected void onPressed()
      {
      if(textureYSize>1)
        {
        setTextureYSize(textureYSize-1);
        }
      }
    };
  textureControlArea.addGuiElement(button);
  
  button = new Button(c3, totalHeight, 12, 12, "+")
    {
    @Override
    protected void onPressed()
      {
      if(textureYSize<1024)
        {
        setTextureYSize(textureYSize+1);
        }
      }
    };
  textureControlArea.addGuiElement(button);
  
  totalHeight+=12;
  
  textureControlArea.setAreaSize(totalHeight);
  }

private void addFileControls()
  {
  
  }
/**
 * add the primitive controls to the primitive control area
 */
private void addPrimitiveControls()
  {
  Primitive p = parent.getPrimitive();
  if(p==null)
    {
    return;
    }
  if(p.getClass()==PrimitiveBox.class)
    {
    addBoxControls();
    }
  else if(p.getClass()==PrimitiveTriangle.class)
    {
    addTriangleControls();
    }
  else if(p.getClass()==PrimitiveQuad.class)
    {
    addQuadControls();
    }
  }

private void addBoxControls()
  {
  /**
   * TODO
   */
  }

private void addTriangleControls()
  {
  /**
   * TODO
   */  
  }

private void addQuadControls()
  {
  /**
   * TODO
   */  
  }

/**
 * add the selectable piece list to the piece-list control area
 */
private void addPieceList()
  {
  ArrayList<ModelPiece> pieces = new ArrayList<ModelPiece>();
  parent.model.getPieces(pieces);
    
  int totalHeight = 3;
  Label label = null;
  
  Listener listener = new Listener(Listener.MOUSE_UP)
    {
    @Override
    public boolean onEvent(GuiElement widget, ActivationEvent evt)
      {
      if(widget.isMouseOverElement(evt.mx, evt.my))
        {
        Label l = (Label)widget;
        if(pieceMap.containsKey(l))
          {
          ModelPiece piece = pieceMap.get(l);
          parent.modelWidget.setSelection(piece, null);          
          }
        else if(primitiveMap.containsKey(l))
          {
          Primitive p = primitiveMap.get(l);
          parent.modelWidget.setSelection(p.parent, p);
          }
        }
      return true;
      }
    };
  
  String prefix;
  int partNum = 1;
  for(ModelPiece piece : pieces)
    {
    partNum = 1;
    label = new Label(3, totalHeight, piece.getName());
    label.addNewListener(listener);
    pieceListArea.addGuiElement(label);
    pieceMap.put(label, piece);
    totalHeight+=12;
    for(Primitive primitive : piece.getPrimitives())
      {
      prefix = primitive.getClass()==PrimitiveBox.class ? "BOX" : primitive.getClass()==PrimitiveQuad.class? "QUAD" : "TRI";
      label = new Label(3, totalHeight, "  "+prefix+":"+partNum);
      label.addNewListener(listener);
      pieceListArea.addGuiElement(label);    
      primitiveMap.put(label, primitive);
      totalHeight+=12;
      partNum++;
      }
    }
  pieceListArea.setAreaSize(totalHeight);
  }

}
