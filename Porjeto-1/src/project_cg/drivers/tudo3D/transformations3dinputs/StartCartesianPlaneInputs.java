package project_cg.drivers.tudo3D.transformations3dinputs;

import project_cg.drivers.tudo3D.geometry3d.planeCartesians3d.CartesianPlane3D;
import view.mainScreen.MainScreen;
import view.mainScreen.MainScreenSingleton;
import view.utils.ShapePanel;

public class StartCartesianPlaneInputs extends ShapePanel {


    @Override
    protected void initializeInputs() {}

    @Override
    protected void onCalculate() {
        MainScreen mainScreen = MainScreenSingleton.getMainScreen();
        CartesianPlane3D plane3D = mainScreen.JPanelHandler.getCartesianPlane3D();

        Thread renderThread = new Thread(plane3D::start);
        renderThread.start();
    }


}
