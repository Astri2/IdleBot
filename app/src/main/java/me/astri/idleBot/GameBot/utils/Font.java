package me.astri.idleBot.GameBot.utils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("unused")
public enum Font {
    a(8,0),  b(8,1),  c(7,2),  d(8,3),   e(8,4),   f(6,5),  g(8,6),  h(8,7),  i(5,8),
    j(6,9),  k(8,10), l(5,11), m(11,12), n(8,13),  o(8,14), p(8,15), q(8,16), r(7,17),
    s(7,18), t(6,19), u(8,20), v(8,21),  w(10,22), x(8,23), y(8,24), z(8,25),
    A(8,26), B(8,27), C(8,28), D(8,29),  E(8,30),  F(8,31), G(8,32), H(8,33), I(5,34),
    J(7,35), K(9,36), L(7,37), M(10,38), N(9,39),  O(9,40), P(8,41), Q(9,42), R(8,43),
    S(7,44), T(9,45), U(8,46), V(9,47),  W(10,48), X(9,49), Y(9,50), Z(8,51),
    ZERO(8,52), ONE(6,53), TWO(8,54),   THREE(8,55), FOUR(8,56),
    FIVE(8,57), SIX(8,58), SEVEN(8,59), EIGHT(8,60), NINE(8,61),
    DOT(5,62), COMAS(5,63), APOSTROPHE(4,64), BRACKETO(6,65), BRACKETC(6,66),
    DOLLAR(8,67), PLUS(8, 68), TILDE(8,69), WHITESPACE(6,70), ERROR(10,71);

    private final int width;
    private final int id;
    private static final ArrayList<BufferedImage> characters = new ArrayList<>();
    private final static HashMap<Character,Font> specialCharacters = new HashMap<>(){{
        put('0', ZERO); put('1', ONE); put('2', TWO); put('3', THREE); put('4', FOUR);
        put('5', FIVE); put('6', SIX); put('7', SEVEN); put('8', EIGHT); put('9', NINE);
        put('.', DOT); put(',', COMAS); put('\'', APOSTROPHE);
        put('(', BRACKETO); put(')', BRACKETC); put('$', DOLLAR); put('+', PLUS);
        put('~', TILDE); put(' ', WHITESPACE);
    }};

    Font(int a, int b) {
        this.width = a;
        this.id = b;
    }

    public static BufferedImage getImage(String input, int zoom, String align) {
        try {
            final int lineShift = 1;
            final int lineHeight = 11;

            ArrayList<ArrayList<Font>> sequence = new ArrayList<>();
            ArrayList<BufferedImage> lines = new ArrayList<>();
            ArrayList<Font> lineFont = new ArrayList<>();
            int maxWidth = 0;
            int imgWidth = 0;
            //get char sequence and image size
            for (char c : input.toCharArray()) {
                if (c == '\n') {
                    BufferedImage lineImg = new BufferedImage(imgWidth + 1, lineHeight, BufferedImage.TYPE_INT_ARGB);
                    lines.add(lineImg);
                    sequence.add(lineFont);
                    lineFont = new ArrayList<>();
                    maxWidth = Math.max(maxWidth, imgWidth + 1);
                    imgWidth = 0;
                } else {
                    Font fontChar = null;
                    try {
                        fontChar = Font.valueOf(String.valueOf(c));
                    } catch (IllegalArgumentException e) { //special or unhandled character
                        fontChar = specialCharacters.get(c);
                        if (fontChar == null) {
                            fontChar = Font.ERROR;
                        }
                    } finally {
                        lineFont.add(fontChar);
                        imgWidth += fontChar.width - 1; //black outline will merge with the next character
                    }
                }
            }
            lines.add(new BufferedImage(imgWidth+1,lineHeight,BufferedImage.TYPE_INT_ARGB));
            sequence.add(lineFont);
            System.out.println(sequence);

            maxWidth = Math.max(maxWidth, imgWidth + 1);
            BufferedImage output = new BufferedImage(maxWidth,
                    lineHeight * lines.size() + lineShift * (lines.size()-1),
                    BufferedImage.TYPE_INT_ARGB);
            Graphics gOutput = output.createGraphics();

            for(int lineId = 0 ; lineId < sequence.size() ; lineId++) {
                //write line
                int x = 0;
                Graphics g = lines.get(lineId).createGraphics();
                for(int i = 0; i < sequence.get(lineId).size() ; i++) {
                    Font c = sequence.get(lineId).get(i);
                    g.drawImage(characters.get(c.id), x, 0, null);
                    x += c.width - 1;
                }
                g.dispose();
                int posX = 0;
                if(align.equals("right"))
                    posX = output.getWidth()-lines.get(lineId).getWidth();
                else  if(align.equals("center")) posX = (output.getWidth()-lines.get(lineId).getWidth())/2;
                //write line on main image

                gOutput.drawImage(lines.get(lineId), posX, lineId*(11+lineShift), null);
            }
            gOutput.dispose();

            //zoom
            if(zoom != 1) {
                BufferedImage zoomedOutput = new BufferedImage(output.getWidth()*zoom,output.getHeight()*zoom,BufferedImage.TYPE_INT_ARGB);
                Graphics g1 = zoomedOutput.createGraphics();
                g1.drawImage(output,0,0,output.getWidth()*zoom,output.getHeight()*zoom, null);
                g1.dispose();
                output = zoomedOutput;
            }

            return output;

        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void init() throws Exception {
        ImageReader r = ImageIO.getImageReadersBySuffix("GIF").next();
        ImageInputStream in = ImageIO.createImageInputStream(new File("app/src/main/resources/font","characters.gif"));
        r.setInput(in);
        for(int i = 0 ; i < r.getNumImages(true) ; i++) {
            characters.add(r.read(i));
        }
        r.dispose();
        in.flush();
        in.close();
    }
}