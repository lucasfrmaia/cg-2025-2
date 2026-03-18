package project_cg.transformations2d;

import project_cg.geometry.points.Point2D;
import project_cg.transformations.BaseTransformation2d;
import utils.Matrix;

public class Reflection  implements BaseTransformation2d {

    public enum Type {
        IN_X,
        IN_Y,
        IN_ORIGIN
    }

    private Type typeTransformation;

    public Reflection(Type value) {
        this.typeTransformation = value;
    }

    public double[][] getTransformation() {
        switch (typeTransformation) {
            case IN_X:
                return getReflectionMatrixInX();
            case IN_Y:
                return getReflectionMatrixInY();
            case IN_ORIGIN:
                return getReflectionMatrixInOrigin();
            default:
                throw new IllegalArgumentException("Tipo de reflexão desconhecido: " + typeTransformation);
        }
    }

    public static double[][] getReflectionMatrixInOrigin() {
        return new double[][]{
                { -1, 0, 0 },
                { 0, -1, 0 },
                { 0, 0, 1 }
        };
    }

    public static double[][] getReflectionMatrixInX() {
        return new double[][]{
                { 1, 0, 0 },
                { 0, -1, 0 },
                { 0, 0, 1 }
        };
    }

    public static double[][] getReflectionMatrixInY() {
        return new double[][]{
                {-1, 0, 0 },
                { 0, 1, 0 },
                { 0, 0, 1 }
        };
    }

    @Override
    public String toString() {
        switch (typeTransformation) {
            case IN_X:
                return "Rx";
            case IN_Y:
                return "Ry";
            case IN_ORIGIN:
                return "Rorigem";
            default:
                return "Tipo de reflexão desconhecido";
        }
    }

}
