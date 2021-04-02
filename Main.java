package com.gridnine.testing;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;

/**
 * Factory class to get sample list of flights.
 */
class FlightBuilder {
    static List<Flight> createFlights() {
        LocalDateTime threeDaysFromNow = LocalDateTime.now().plusDays(3);
        return Arrays.asList(
                //A normal flight with two hour duration
                createFlight(threeDaysFromNow, threeDaysFromNow.plusHours(2)),
                //A normal multi segment flight
                createFlight(threeDaysFromNow, threeDaysFromNow.plusHours(2),
                        threeDaysFromNow.plusHours(3), threeDaysFromNow.plusHours(5)),
                //A flight departing in the past
                createFlight(threeDaysFromNow.minusDays(6), threeDaysFromNow),
                //A flight that departs before it arrives
                createFlight(threeDaysFromNow, threeDaysFromNow.minusHours(6)),
                //A flight with more than two hours ground time
                createFlight(threeDaysFromNow, threeDaysFromNow.plusHours(2),
                        threeDaysFromNow.plusHours(5), threeDaysFromNow.plusHours(6)),
                //Another flight with more than two hours ground time
                createFlight(threeDaysFromNow, threeDaysFromNow.plusHours(2),
                        threeDaysFromNow.plusHours(3), threeDaysFromNow.plusHours(4),
                        threeDaysFromNow.plusHours(6), threeDaysFromNow.plusHours(7)));
    }

    private static Flight createFlight(final LocalDateTime... dates) {
        if ((dates.length % 2) != 0) {
            throw new IllegalArgumentException(
                    "you must pass an even number of dates");
        }
        List<Segment> segments = new ArrayList<>(dates.length / 2);
        for (int i = 0; i < (dates.length - 1); i += 2) {
            segments.add(new Segment(dates[i], dates[i + 1]));
        }
        return new Flight(segments);
    }
}

/**
 * Bean that represents a flight.
 */
class Flight {
    private final List<Segment> segments;

    Flight(final List<Segment> segs) {
        segments = segs;
    }

    List<Segment> getSegments() {
        return segments;
    }

    @Override
    public String toString() {
        return segments.stream().map(Object::toString)
                .collect(Collectors.joining(" "));
    }
}

/**
 * Bean that represents a flight segment.
 */
class Segment {
    private final LocalDateTime departureDate;

    private final LocalDateTime arrivalDate;

    Segment(final LocalDateTime dep, final LocalDateTime arr) {
        departureDate = Objects.requireNonNull(dep);
        arrivalDate = Objects.requireNonNull(arr);
    }

    LocalDateTime getDepartureDate() {
        return departureDate;
    }

    LocalDateTime getArrivalDate() {
        return arrivalDate;
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt =
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        return '[' + departureDate.format(fmt) + '|' + arrivalDate.format(fmt)
                + ']';
    }
}

public class Main {

    private static ArrayList<Date> sfx2 = new ArrayList<>();
    private static ArrayList<String> evg2 = new ArrayList<>();

    public static void main(String[] args) throws ParseException {
        main();
    }

    public static void main() throws ParseException {
        String [] alldates = new String[0];
        String [] alldates1 = new String[0];
        String alldates2;
        ArrayList<Date> sfx = new ArrayList<>();
        ArrayList<Integer> evg = new ArrayList<>();
        for(Flight z : FlightBuilder.createFlights()) {
            alldates =  z.toString().split(" ");
            evg.add(alldates.length);
            for(String z1 : alldates) {
                alldates1 =  z1.split("\\|");
                for(String z2 : alldates1) {
                    alldates2 =  z2.replace("[","").replace("]","");
                    String stringDate=alldates2;
                    Date date=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(stringDate);
                    sfx.add(date);
                }
            }
        }

        //1.	вылет до текущего момента времени
        sfx2.clear();
        evg2.clear();
        pravilo1(sfx);
        printrez(sfx2, evg);
        System.out.println("1.Исключены перелёты с вылетом до текущего момента времени");
        for(String z5 : evg2) System.out.println(z5);

        //2.	имеются сегменты с датой прилёта раньше даты вылета
        sfx2.clear();
        evg2.clear();
        pravilo2(sfx);
        printrez(sfx2, evg);
        System.out.println("2.Исключены сегменты с датой прилёта раньше даты вылета");
        for(String z6 : evg2) System.out.println(z6);

        //3.	общее время, проведённое на земле превышает два часа (время на земле — это интервал между прилётом одного сегмента и вылетом следующего за ним)
        sfx2.clear();
        evg2.clear();
        pravilo3(sfx, evg);
        printrez(sfx2, evg);
        System.out.println("3.Исключены перелёты, в которых общее время, проведённое на земле превышает два часа");
        for(String z7 : evg2) System.out.println(z7);
    }

    public static ArrayList<Date> pravilo1( ArrayList<Date> sfx) throws ParseException {
        Date dateNow = new Date();
        for(int i = 0; i < sfx.size() - 1; i = i + 2) {
            long razvv = sfx.get(i).getTime() - dateNow.getTime();
            if(razvv >= 0) {
                //System.out.println("Все правильно");
                sfx2.add(sfx.get(i));
                sfx2.add(sfx.get(i + 1));
            }
            else {
                //System.out.println("вылет до текущего момента времени");
                sfx2.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse("0000-00-00T00:00"));
                sfx2.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse("0000-00-00T00:00"));
            }
        }
        return sfx2;
    }

    public static ArrayList<Date> pravilo2( ArrayList<Date> sfx) throws ParseException {
        for(int i = 0; i < sfx.size() - 1; i = i + 2) {
            long razvv = sfx.get(i + 1).getTime() - sfx.get(i).getTime();
            if(razvv > 0) {
                //System.out.println("Все правильно");
                sfx2.add(sfx.get(i));
                sfx2.add(sfx.get(i + 1));
            }
            else {
                //System.out.println("дата прилёта раньше даты вылета");
                sfx2.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse("0000-00-00T00:00"));
                sfx2.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse("0000-00-00T00:00"));
            }
        }
        return sfx2;
    }

    public static ArrayList<Date> pravilo3( ArrayList<Date> sfx, ArrayList<Integer> evg) throws ParseException {
        int i = 0;
        for(Integer z3 : evg) {
            if(z3 == 1 & i < sfx.size() / 2 - 1) {
                sfx2.add(sfx.get(2 * i));
                sfx2.add(sfx.get(2 * i + 1));
                i = i + 1;
            }
            else if(z3 == 2 & i < sfx.size() / 2 - 1) {
                long razvv = sfx.get(2 * i + 2).getTime() - sfx.get(2 * i + 1).getTime();
                if (razvv <= 2 * 60 * 60 * 1000) {
                    //System.out.println("Все правильно");
                    sfx2.add(sfx.get(2 * i));
                    sfx2.add(sfx.get(2 * i + 1));
                    sfx2.add(sfx.get(2 * i + 2));
                    sfx2.add(sfx.get(2 * i + 3));
                }
                else {
                    //System.out.println("общее время, проведённое на земле превышает два часа");
                    sfx2.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse("0000-00-00T00:00"));
                    sfx2.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse("0000-00-00T00:00"));
                    sfx2.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse("0000-00-00T00:00"));
                    sfx2.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse("0000-00-00T00:00"));
                }
                i = i + 2;
            }
            else if(z3 == 3 & i < sfx.size() / 2 - 1) {
                long razvv = sfx.get(2 * i + 2).getTime() - sfx.get(2 * i + 1).getTime();
                if (razvv <= 2 * 60 * 60 * 1000) {
                    //System.out.println("Все правильно");
                    sfx2.add(sfx.get(2 * i));
                    sfx2.add(sfx.get(2 * i + 1));
                    sfx2.add(sfx.get(2 * i + 2));
                    sfx2.add(sfx.get(2 * i + 3));
                }
                else {
                    //System.out.println("общее время, проведённое на земле превышает два часа");
                    sfx2.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse("0000-00-00T00:00"));
                    sfx2.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse("0000-00-00T00:00"));
                    sfx2.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse("0000-00-00T00:00"));
                    sfx2.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse("0000-00-00T00:00"));
                }
                long razvv2 = sfx.get(2 * i + 4).getTime() - sfx.get(2 * i + 3).getTime();
                if (razvv2 <= 2 * 60 * 60 * 1000) {
                    //System.out.println("Все правильно");
                    sfx2.add(sfx.get(2 * i + 4));
                    sfx2.add(sfx.get(2 * i + 5));
                }
                else {
                    //System.out.println("общее время, проведённое на земле превышает два часа");
                    sfx2.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse("0000-00-00T00:00"));
                    sfx2.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse("0000-00-00T00:00"));
                }
                i = i + 3;
            }
        }
        return sfx2;
    }

    public static ArrayList<String> printrez(ArrayList<Date> sfx2, ArrayList<Integer> evg) throws ParseException {
        int i = 0;
        for(Integer z4 : evg) {
            for(int j = i; j < i + 2 * z4; j = j + 2) {
                if (sfx2.get(i).equals(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse("0000-00-00T00:00"))) {

                } else {
                    String rez = sfx2.get(j).toString() + "|" + sfx2.get(j + 1).toString();
                    evg2.add(rez);
                    if(i + 2 * z4 - j <= 2) evg2.add("\n");
                }
            }
            i = i + 2 * z4;
        }
        return evg2;
    }

}
