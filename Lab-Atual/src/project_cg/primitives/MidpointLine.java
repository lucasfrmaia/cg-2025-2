package project_cg.primitives;

import project_cg.geometry.points.Point2D;
import project_cg.primitives.bases.BaseLine;
import project_cg.primitives.bases.BasePrimitives;

import java.util.function.Consumer;


public class MidpointLine extends BaseLine {


    public MidpointLine(Consumer<Point2D> callback) {
        super(callback);
    }

    public MidpointLine() {}

    // Função para determinar o octante e desenhar a linha usando pontos Point2D
    @Override
    public void desenhaLinha (Point2D start, Point2D end) {
        int x1 = (int) start.x;
        int y1 = (int) start.y;
        int x2 = (int) end.x;
        int y2 = (int) end.y;

        int dx = x2 - x1;
        int dy = y2 - y1;

        // Caso especial: Linha vertical (dx = 0)
        if (dx == 0) {
            desenhaLinhaVertical(start, end);
            return;
        }

        // Caso especial: Linha horizontal (dy = 0)
        if (dy == 0) {
            desenhaLinhaHorizontal(start, end);
            return;
        }

        // Identifica o octante baseado em dx e dy
        int octante = getOctante(dx, dy);

        // Aplica a lógica de acordo com o octante
        switch (octante) {
            case 1:
                desenhaOctante1(start, end);
                break;
            case 2:
                desenhaOctante2(start, end);
                break;
            case 3:
                desenhaOctante3(start, end);
                break;
            case 4:
                desenhaOctante4(start, end);
                break;
            case 5:
                desenhaOctante5(start, end);
                break;
            case 6:
                desenhaOctante6(start, end);
                break;
            case 7:
                desenhaOctante7(start, end);
                break;
            case 8:
                desenhaOctante8(start, end);
                break;
        }
    }

    // Função para desenhar uma linha vertical usando Point2D
    private void desenhaLinhaVertical(Point2D start, Point2D end) {
        int x = (int) start.x;
        int yStart = (int) Math.min(start.y, end.y);
        int yEnd = (int) Math.max(start.y, end.y);

        for (int y = yStart; y <= yEnd; y++) {
            callback.accept(new Point2D(x, y));
        }
    }

    // Função para desenhar uma linha horizontal usando Point2D
    private void desenhaLinhaHorizontal(Point2D start, Point2D end) {
        int y = (int) start.y;
        int xStart = (int) Math.min(start.x, end.x);
        int xEnd = (int) Math.max(start.x, end.x);

        for (int x = xStart; x <= xEnd; x++) {
            callback.accept(new Point2D(x, y));
        }
    }

    // Determina em qual octante a linha está
    private int getOctante(int dx, int dy) {
        if (Math.abs(dy) <= Math.abs(dx)) {
            if (dx > 0 && dy >= 0) return 1;  // Octante 1
            if (dx > 0 && dy < 0) return 8;   // Octante 8
            if (dx < 0 && dy >= 0) return 4;  // Octante 4
            if (dx < 0 && dy < 0) return 5;   // Octante 5
        } else {
            if (dx > 0 && dy > 0) return 2;   // Octante 2
            if (dx < 0 && dy > 0) return 3;   // Octante 3
            if (dx > 0 && dy < 0) return 7;   // Octante 7
            if (dx < 0 && dy < 0) return 6;   // Octante 6
        }
        return 1; // Octante padrão
    }

    // Implementações para cada octante, agora com Point2D
    private void desenhaOctante1(Point2D start, Point2D end) {
        int x1 = (int) start.x, y1 = (int) start.y, x2 = (int) end.x, y2 = (int) end.y;
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int d = 2 * dy - dx;
        int y = y1;

        for (int x = x1; x <= x2; x++) {
            callback.accept(new Point2D(x, y));
            if (d > 0) {
                y++;
                d -= 2 * dx;
            }
            d += 2 * dy;
        }
    }

    private void desenhaOctante2(Point2D start, Point2D end) {
        int x1 = (int) start.x, y1 = (int) start.y, x2 = (int) end.x, y2 = (int) end.y;
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int d = 2 * dx - dy;
        int x = x1;

        for (int y = y1; y <= y2; y++) {
            callback.accept(new Point2D(x, y));
            if (d > 0) {
                x++;
                d -= 2 * dy;
            }
            d += 2 * dx;
        }
    }

    private void desenhaOctante3(Point2D start, Point2D end) {
        int x1 = (int) start.x, y1 = (int) start.y, x2 = (int) end.x, y2 = (int) end.y;
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int d = 2 * dx - dy;
        int x = x1;

        for (int y = y1; y <= y2; y++) {
            callback.accept(new Point2D(x, y));
            if (d > 0) {
                x--;
                d -= 2 * dy;
            }
            d += 2 * dx;
        }
    }

    private void desenhaOctante4(Point2D start, Point2D end) {
        int x1 = (int) start.x, y1 = (int) start.y, x2 = (int) end.x, y2 = (int) end.y;
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int d = 2 * dy - dx;
        int y = y1;

        for (int x = x1; x >= x2; x--) {
            callback.accept(new Point2D(x, y));
            if (d > 0) {
                y++;
                d -= 2 * dx;
            }
            d += 2 * dy;
        }
    }

    private void desenhaOctante5(Point2D start, Point2D end) {
        int x1 = (int) start.x, y1 = (int) start.y, x2 = (int) end.x, y2 = (int) end.y;
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int d = 2 * dy - dx;
        int y = y1;

        for (int x = x1; x >= x2; x--) {
            callback.accept(new Point2D(x, y));
            if (d > 0) {
                y--;
                d -= 2 * dx;
            }
            d += 2 * dy;
        }
    }

    private void desenhaOctante6(Point2D start, Point2D end) {
        int x1 = (int) start.x, y1 = (int) start.y, x2 = (int) end.x, y2 = (int) end.y;
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int d = 2 * dx - dy;
        int x = x1;

        for (int y = y1; y >= y2; y--) {
            callback.accept(new Point2D(x, y));
            if (d > 0) {
                x--;
                d -= 2 * dy;
            }
            d += 2 * dx;
        }
    }

    private void desenhaOctante7(Point2D start, Point2D end) {
        int x1 = (int) start.x, y1 = (int) start.y, x2 = (int) end.x, y2 = (int) end.y;
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int d = 2 * dx - dy;
        int x = x1;

        for (int y = y1; y >= y2; y--) {
            callback.accept(new Point2D(x, y));
            if (d > 0) {
                x++;
                d -= 2 * dy;
            }
            d += 2 * dx;
        }
    }

    private void desenhaOctante8(Point2D start, Point2D end) {
        int x1 = (int) start.x, y1 = (int) start.y, x2 = (int) end.x, y2 = (int) end.y;
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int d = 2 * dy - dx;
        int y = y1;

        for (int x = x1; x <= x2; x++) {
            callback.accept(new Point2D(x, y));
            if (d > 0) {
                y--;
                d -= 2 * dx;
            }
            d += 2 * dy;
        }
    }

}

