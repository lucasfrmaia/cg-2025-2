package project_cg.geometry.clipping;

import project_cg.geometry.points.Point2D;

public class SutherlandHodgmanLineClipper {

    // Equivalente ao enum Boundary do C++ (usado como índices de array também)
    private static final int Left = 0;
    private static final int Right = 1;
    private static final int Bottom = 2;
    private static final int Top = 3;
    
    private static final int nClip = 4;

    private Point2D wMin;
    private Point2D wMax;

    public SutherlandHodgmanLineClipper(int xMin, int yMin, int xMax, int yMax) {
        this.wMin = new Point2D(xMin, yMin);
        this.wMax = new Point2D(xMax, yMax);
    }

    /**
     * Função principal equivalente a polygonClipSuthHodg.
     * Modificada para retornar os dois pontos da reta recortada, integrando ao seu projeto.
     */
    public Point2D[] clipLine(int x1, int y1, int x2, int y2) {
        Point2D[] pIn = new Point2D[]{new Point2D(x1, y1), new Point2D(x2, y2)};
        Point2D[] pOut = new Point2D[10]; // Buffer de saída 
        
        // Parameter "first" holds pointer to first point processed for a boundary; 
        // "s" holds most recent point processed for boundary.
        Point2D[] first = new Point2D[nClip];
        Point2D[] s = new Point2D[nClip];
        
        // Usamos um array de 1 posição para simular a passagem de ponteiro (&cnt) do C++
        int[] cnt = {0};

        // Envia os vértices da linha para o pipeline de recorte
        for (int k = 0; k < pIn.length; k++) {
            clipPoint(pIn[k], Left, wMin, wMax, pOut, cnt, first, s);
        }
        
        // Fecha o polígono/linha recortada
        closeClip(wMin, wMax, pOut, cnt, first, s);

        if (cnt[0] >= 2) {
            // Se sobraram pelo menos 2 pontos, retorna a linha recortada
            return new Point2D[]{pOut[0], pOut[1]};
        }

        return null; // Linha foi totalmente descartada
    }

    private boolean inside(Point2D p, int b, Point2D wMin, Point2D wMax) {
        switch (b) {
            case Left:   if (p.x < wMin.x) return false; break;
            case Right:  if (p.x > wMax.x) return false; break;
            case Bottom: if (p.y < wMin.y) return false; break;
            case Top:    if (p.y > wMax.y) return false; break;
        }
        return true;
    }

    private boolean cross(Point2D p1, Point2D p2, int winEdge, Point2D wMin, Point2D wMax) {
        if (inside(p1, winEdge, wMin, wMax) == inside(p2, winEdge, wMin, wMax)) {
            return false;
        } else {
            return true;
        }
    }

    private Point2D intersect(Point2D p1, Point2D p2, int winEdge, Point2D wMin, Point2D wMax) {
        double iPtX = 0, iPtY = 0;
        double m = 0;

        if (p1.x != p2.x) {
            m = (double) (p1.y - p2.y) / (p1.x - p2.x);
        }

        switch (winEdge) {
            case Left:
                iPtX = wMin.x;
                iPtY = p2.y + (wMin.x - p2.x) * m;
                break;
            case Right:
                iPtX = wMax.x;
                iPtY = p2.y + (wMax.x - p2.x) * m;
                break;
            case Bottom:
                iPtY = wMin.y;
                if (p1.x != p2.x) iPtX = p2.x + (wMin.y - p2.y) / m;
                else iPtX = p2.x;
                break;
            case Top:
                iPtY = wMax.y;
                if (p1.x != p2.x) iPtX = p2.x + (wMax.y - p2.y) / m;
                else iPtX = p2.x;
                break;
        }

        // Arredondando para int pois seu Point2D usa inteiros
        return new Point2D((int) Math.round(iPtX), (int) Math.round(iPtY));
    }

    private void clipPoint(Point2D p, int winEdge, Point2D wMin, Point2D wMax, Point2D[] pOut, int[] cnt, Point2D[] first, Point2D[] s) {
        Point2D iPt;

        /* If no previous point exists for this clipping boundary, save this point. */
        if (first[winEdge] == null) {
            first[winEdge] = p;
        } else {
            /* Previous point exists. If p and previous point cross this clipping boundary, 
             * find intersection. Clip against next boundary, if any. 
             * If no more clip boundaries, add intersection to output list. */
            if (cross(p, s[winEdge], winEdge, wMin, wMax)) {
                iPt = intersect(p, s[winEdge], winEdge, wMin, wMax);
                if (winEdge < Top) {
                    clipPoint(iPt, winEdge + 1, wMin, wMax, pOut, cnt, first, s);
                } else {
                    pOut[cnt[0]] = iPt;
                    cnt[0]++;
                }
            }
        }

        /* Save p as most recent point for this clip boundary. */
        s[winEdge] = p;

        /* For all, if point inside, proceed to next boundary, if any. */
        if (inside(p, winEdge, wMin, wMax)) {
            if (winEdge < Top) {
                clipPoint(p, winEdge + 1, wMin, wMax, pOut, cnt, first, s);
            } else {
                pOut[cnt[0]] = p;
                cnt[0]++;
            }
        }
    }

    private void closeClip(Point2D wMin, Point2D wMax, Point2D[] pOut, int[] cnt, Point2D[] first, Point2D[] s) {
        Point2D pt;
        int winEdge;

        for (winEdge = Left; winEdge <= Top; winEdge++) {
            // Precisamos garantir que first e s não são nulos antes de cruzar
            if (s[winEdge] != null && first[winEdge] != null) {
                if (cross(s[winEdge], first[winEdge], winEdge, wMin, wMax)) {
                    pt = intersect(s[winEdge], first[winEdge], winEdge, wMin, wMax);
                    
                    if (winEdge < Top) {
                        clipPoint(pt, winEdge + 1, wMin, wMax, pOut, cnt, first, s);
                    } else {
                        pOut[cnt[0]] = pt;
                        cnt[0]++;
                    }
                }
            }
        }
    }
}