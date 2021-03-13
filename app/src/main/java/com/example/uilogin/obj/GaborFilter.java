package com.example.uilogin.obj;

import android.net.Uri;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import static org.opencv.core.CvType.CV_32F;

public class GaborFilter {
    private double mean;
    private double entropy;
    private Mat gaborMat = new Mat();

    public GaborFilter(){

    }

    public void extract(Uri img, double sigma, double theta, double lambd, double gamma, double psi)  {
            String path = img.getPath();
            //preprocessing
            Mat imgMat = Imgcodecs.imread(path, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
            Size sz = new Size(700,700);
            Imgproc.resize(imgMat , imgMat, sz );
            Imgproc.equalizeHist(imgMat,imgMat);

            //2dgabor
            Mat dstGabor = new Mat();
            Mat s = new Mat(9,9,CV_32F);
            Point anchor = new Point(-1, -1);
            double delta = 0.0;

            Mat gabKernel = Imgproc.getGaborKernel(s.size(), sigma, theta, lambd, gamma, psi,CV_32F);
            Imgproc.filter2D(imgMat, dstGabor, CV_32F, gabKernel, anchor, delta, Core.BORDER_DEFAULT);

            Mat finalMat = new Mat();
            Core.normalize(dstGabor, finalMat, 0, 255, Core.NORM_MINMAX,CV_32F);
            finalMat.convertTo(finalMat,CV_32F);

        this.gaborMat = finalMat;
        double[][]gaborMat2 = new double[gaborMat.width()][gaborMat.height()];
        for(int i=0;i<gaborMat.width();i++){
            for(int j=0;j<gaborMat.height();j++){
                gaborMat2[i][j]=gaborMat.get(j,i)[0];
            }
        }
        this.entropy = calcEntropy();
        this.mean = calcMean();
    }

    private double calcEntropy(){
        Mat ln = new Mat(gaborMat.size(),gaborMat.type());
        Core.log(gaborMat, ln);
       // Core.patchNaNs(ln,4);
        Mat squared = new Mat(gaborMat.size(),gaborMat.type());
        Core.multiply(ln, gaborMat, squared, -1);
       Core.patchNaNs(squared);
        Scalar sum = Core.sumElems(squared);

        return sum.val[0];
    }
    private double calcEntropy(double[][] matrix) {
        double temp = 0;
        double sum = 0;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] != 0) {
                    sum +=matrix[i][j];
                }
            }
        }
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] != 0) {
                    temp += (Math.pow((matrix[i][j]/sum),2)* Math.log10(matrix[i][j]/sum)) * -1;
                }
            }
        }
        return temp;
    }
    //mean
    private double calcMean(double[][] matrix) {
        double temp = 0;
        double sum=0;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] != 0) {
                    temp += (matrix[i][j] * (matrix[i][j]));
                    sum +=matrix[i][j];
                }
            }
        }
        double hasil = temp/sum;
        return hasil;
    }
    private double calcMean()
    {
        return  Core.mean(gaborMat).val[0];
    }
    public double getEntropy()
    {
        return entropy;
    }
    public double getMean() {
        return mean;
    }
    public Mat getMat(){
        return gaborMat;
    }
}
