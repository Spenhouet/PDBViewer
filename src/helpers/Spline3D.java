package helpers;

import javafx.geometry.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

/**
 * From: http://www.java-gaming.org/index.php?topic=9830.0
 */
public class Spline3D {
    private static final Object[] EMPTYOBJLIST = new Object[]{};
    private static final String VECTOR_3_DGET_X_METHOD_NAME = "getX";
    private static final String VECTOR_3_DGET_Y_METHOD_NAME = "getY";
    private static final String VECTOR_3_DGET_Z_METHOD_NAME = "getZ";
    private final List<Point3D> points = new ArrayList<>();
    private final List<Cubic> xCubics = new ArrayList<>();
    private final List<Cubic> yCubics = new ArrayList<>();
    private final List<Cubic> zCubics = new ArrayList<>();
    private Method vector2DgetXMethod;
    private Method vector2DgetYMethod;
    private Method vector2DgetZMethod;

    private Spline3D() {
        try {
            vector2DgetXMethod = Point3D.class.getDeclaredMethod(VECTOR_3_DGET_X_METHOD_NAME);
            vector2DgetYMethod = Point3D.class.getDeclaredMethod(VECTOR_3_DGET_Y_METHOD_NAME);
            vector2DgetZMethod = Point3D.class.getDeclaredMethod(VECTOR_3_DGET_Z_METHOD_NAME);
        } catch (SecurityException | NoSuchMethodException e) {
            // Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Spline3D(List<Point3D> points) {
        this();
        addPoints(points);
    }

    public void addPoint(Point3D point) {
        this.points.add(point);
        calcSpline();
    }

    private void addPoints(List<Point3D> pointList) {
        this.points.addAll(pointList);
        calcSpline();
    }

    private void calcSpline() {
        try {
            calcNaturalCubic(points, vector2DgetXMethod, xCubics);
            calcNaturalCubic(points, vector2DgetYMethod, yCubics);
            calcNaturalCubic(points, vector2DgetZMethod, zCubics);
        } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
            // Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Stream<Point3D> stream(int fillFactor) {
        int generate = points.size() * fillFactor;
        return IntStream.range(0, generate)
                .mapToDouble(index -> (double) index / (double) generate)
                .mapToObj(this::getPoint);
    }

    public Point3D getPoint(double position) {
        position = position * xCubics.size();
        int cubicNum = (int) position;
        double cubicPos = (position - cubicNum);

        return new Point3D(xCubics.get(cubicNum)
                .eval(cubicPos),
                yCubics.get(cubicNum)
                        .eval(cubicPos),
                zCubics.get(cubicNum)
                        .eval(cubicPos));
    }

    private void calcNaturalCubic(List valueCollection, Method getVal, Collection<Cubic> cubicCollection) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        int num = valueCollection.size() - 1;

        double[] gamma = new double[num + 1];
        double[] delta = new double[num + 1];
        double[] derivatives = new double[num + 1];

        int i;
        /*
           We solve the equation
          [2 1       ] [derivatives[0]]   [3(x[1] - x[0])  ]
          |1 4 1     | |derivatives[1]|   |3(x[2] - x[0])  |
          |  1 4 1   | | .  | = |      .         |
          |    ..... | | .  |   |      .         |
          |     1 4 1| | .  |   |3(x[n] - x[n-2])|
          [       1 2] [derivatives[n]]   [3(x[n] - x[n-1])]

          by using row operations to convert the matrix to upper triangular
          and then back sustitution.  The derivatives[i] are the derivatives at the knots.
        */
        gamma[0] = 1.0f / 2.0f;
        for (i = 1; i < num; i++) {
            gamma[i] = 1.0f / (4.0f - gamma[i - 1]);
        }
        gamma[num] = 1.0f / (2.0f - gamma[num - 1]);

        Double p0 = (Double) getVal.invoke(valueCollection.get(0), EMPTYOBJLIST);
        Double p1 = (Double) getVal.invoke(valueCollection.get(1), EMPTYOBJLIST);

        delta[0] = 3.0 * (p1 - p0) * gamma[0];
        for (i = 1; i < num; i++) {
            p0 = (Double) getVal.invoke(valueCollection.get(i - 1), EMPTYOBJLIST);
            p1 = (Double) getVal.invoke(valueCollection.get(i + 1), EMPTYOBJLIST);
            delta[i] = (3.0 * (p1 - p0) - delta[i - 1]) * gamma[i];
        }
        p0 = (Double) getVal.invoke(valueCollection.get(num - 1), EMPTYOBJLIST);
        p1 = (Double) getVal.invoke(valueCollection.get(num), EMPTYOBJLIST);

        delta[num] = (3.0 * (p1 - p0) - delta[num - 1]) * gamma[num];

        derivatives[num] = delta[num];
        for (i = num - 1; i >= 0; i--) {
            derivatives[i] = delta[i] - gamma[i] * derivatives[i + 1];
        }

        /*
            now compute the coefficients of the cubics
        */
        cubicCollection.clear();

        for (i = 0; i < num; i++) {
            p0 = (Double) getVal.invoke(valueCollection.get(i), EMPTYOBJLIST);
            p1 = (Double) getVal.invoke(valueCollection.get(i + 1), EMPTYOBJLIST);

            cubicCollection.add(new Cubic(
                            p0,
                    derivatives[i],
                    3 * (p1 - p0) - 2 * derivatives[i] - derivatives[i + 1],
                    2 * (p0 - p1) + derivatives[i] + derivatives[i + 1]
                    )
            );
        }
    }

    class Cubic {
        private final double a;
        private final double b;
        private final double c;
        private final double d;

        Cubic(double a, double b, double c, double d) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
        }

        double eval(double u) {
            return (((d * u) + c) * u + b) * u + a;
        }
    }
}
