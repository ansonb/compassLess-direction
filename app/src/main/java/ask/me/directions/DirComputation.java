package ask.me.directions;

import java.util.Date;

/**
 * Created by anson on 24/9/16.
 */
public class DirComputation {

    static final double pi = 3.141572;
    static final long r = 6371000; //radius of earth in m
    static final double ro = 149.6; //radius of orbit in AU

    static double i = 0;
    static double theeta = 0;
    static double phi = 0;
    static float h = 1;

    static int day, month, hrs;

    static double theetaO = 0; //wrt line joining sun and earth's pos on july 3
    static final double theetaD = 0.01733; //angle covered by earth in a day in radians

    public static double getDirection(double i, int time, double lat){
        i = deg2rad(i);

        hrs = time;

        phi = deg2rad(lat);

        Date date = new Date();
        day = date.getDate();
        month = date.getMonth();
        // hrs = date.getHours();

        theetaO = getDaysFrJuly3(day, month)*theetaD;
        theeta = getTheeta();

        return computeAngle();
    }

//    private static double getTheeta(double time){
//        return (time-6)*360/24;
//    }

    private static double computeAngle(){
        double c1 = Math.cos(theeta);
        double s1 = Math.sin(theeta);
        double c2 = Math.cos(phi);
        double s2 = Math.sin(phi);
        double ci = Math.cos(i);
        double si = Math.sin(i);

        double p0[] = {r*c1*c2, r*s1*c2, r*s2};
        double n[] = {c1*c2, s1*c2, s2};
        double l0[] = {(r+h)*c1*c2, (r+h)*s1*c2, (r+h)*s2};
        double l[] = {get_xo(theetaO)/ro, get_yo(theetaO)*ci/ro, get_yo(theetaO)*si/ro};

        double d  = getScalar_d(p0,l0,n,l);

        double p[] = add(mult(d,l),l0);

        System.out.println("d "+d);

        double proj[] = sub(p,p0);
        double lat[] = {-s1, c1, 0};
        double result = dotProduct(proj,lat)/(magnitude(proj)*magnitude(lat));

        if(proj[2]<0){
            //fshadow acing south
            return 180 - ( rad2deg(Math.acos(result))-90 );
        }else{
            //shadow facing north
            return rad2deg(Math.acos(result))-90;
        }


    }

    private static double rad2deg(double a){
        return a*180/pi;
    }

    private static double deg2rad(double angle){
        return angle*pi/180;
    }

    //------- vector functions----------------
    static double dotProduct(double arr1[], double arr2[]){
        return (arr1[0]*arr2[0]+arr1[1]*arr2[1]+arr1[2]*arr2[2]);
    }

    static double magnitude(double arr[]){
        return Math.abs(Math.sqrt(arr[0]*arr[0]+arr[1]*arr[1]+arr[2]*arr[2]));
    }

    static double[] sub(double arr1[], double arr2[]){
        double arr[] = {arr1[0]-arr2[0], arr1[1]-arr2[1], arr1[2]-arr2[2]};
        return arr;
    }

    static double[] add(double arr1[], double arr2[]){
        double arr[] = {arr1[0]+arr2[0], arr1[1]+arr2[1], arr1[2]+arr2[2]};
        return arr;
    }

    static double[] mult(double scalar, double arr1[]){
        double arr[] = {scalar*arr1[0], scalar*arr1[1], scalar*arr1[2]};
        return arr;
    }

    //-------------------------------------
    static double get_xo(double a){
        return ro*Math.sin(a);
    }

    static double get_yo(double a){
        return -ro*Math.cos(a);
    }

    static double getTheetaO(int day, int month){
        return getDaysFrJuly3(day, month)*theetaD;
    }

    static double getTheeta(){
        return getTheetaO(day, month)+deg2rad((hrs-6)*360/24);
    }

    static int getDaysFrJuly3(int day, int month){
        int result = 0;
        switch(month){
            case 5:
                result += 30;
            case 4:
                result += 31;
            case 3:
                result += 30;
            case 2:
                result += 31;
            case 1:
                result += 28;
            case 0:
                result += 31;
            case 11:
                result += 31;
            case 10:
                result += 30;
            case 9:
                result += 31;
            case 8:
                result += 30;
            case 7:
                result += 31;
            case 6:
                result += 30;
            default: break;
        }

        result = result-3-(getDaysInMnth(month)-day);

        return result;
    }

    static int getDaysInMnth(int month){

        switch(month){
            case 5:
                return 30;
            case 4:
                return 31;
            case 3:
                return 30;
            case 2:
                return 31;
            case 1:
                return 28;
            case 0:
                return 31;
            case 11:
                return 31;
            case 10:
                return 30;
            case 9:
                return 31;
            case 8:
                return 30;
            case 7:
                return 31;
            case 6:
                return 30;
            default:
                return 0;
        }
    }

    static double getScalar_d(double p0[], double l0[], double n[], double l[]){
        return dotProduct( sub(p0,l0),n )/dotProduct( l,n );
    }
}
