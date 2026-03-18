package project_cg.transformations2d;

import project_cg.geometry.points.Point2D;
import project_cg.transformations.BaseTransformation2d;
import utils.Matrix;


public class Shear implements BaseTransformation2d { //Cisalhamento

        public enum Type {
                IN_X,
                IN_Y,
                IN_XY
        }

        private final Type type;
        private final double a;
        private final double b;

        public Shear(Type type, double a, double b) {
                this.type = type;
                this.a = a;
                this.b = b;
        }

        @Override
        public double[][] getTransformation() {
                switch (type) {
                        case IN_X:
                                return getMatrixShearX(a);
                        case IN_Y:
                                return getMatrixShearY(b);
                        case IN_XY:
                                return getMatrixShearXY(a, b);
                        default:
                                throw new IllegalArgumentException("Tipo de cisalhamento desconhecido: " + type);
                }
        }


    public static double[][] getMatrixShearY(double shy) {
        return new double[][] {
                { 1, 0, 0 },
                { shy, 1, 0 },   // Shear along Y-axis
                { 0, 0, 1 }
        };
    }

    public static double[][] getMatrixShearX(double shx) {
        return new double[][] {
                { 1, shx, 0 },   // Shear along X-axis
                { 0, 1, 0 },
                { 0, 0, 1 }
        };
    }

    public static double[][] getMatrixShearXY(double shx, double shy) {
        return new double[][] {
                { 1, shx, 0 },   // Shear along X-axis
                { shy, 1, 0 },   // Shear along Y-axis
                { 0, 0, 1 }
        };
    }

        @Override
        public String toString() {
                switch (type) {
                        case IN_X:
                                return "ShX(" + a + ")";
                        case IN_Y:
                                return "ShY(" + b + ")";
                        case IN_XY:
                                return "ShXY(" + a + ", " + b + ")";
                        default:
                                return "Sh(?)";
                }
        }


}
