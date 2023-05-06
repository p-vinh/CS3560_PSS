import java.text.ParseException;
import java.text.SimpleDateFormat;

/** Class for checking valid dates and task overlaps */
public class Calendar {
    
    /** Checks if given date is valid on the calendar.
        @param  date The date to be checked. 
        @return  True if the date is valid, false otherwise. */
    public boolean isValidDate(int date) {
        String dateString = Integer.toString(date);
        if (dateString.length() != 8) {
            return false;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(dateString);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /** Checks if the two given tasks overlap by time and day.
        @param  firstTask The first task.
        @param  secondTask The second task.
        @return  True if the two tasks overlap, false otherwise. */
    public boolean checkOverlap(Task firstTask, Task secondTask) {
        int firstTaskEndDate = firstTask.getDate();
        int secondTaskEndDate = secondTask.getDate();
    
        // Check if the tasks have at least a 2 day gap
        if (Math.abs(firstTask.getDate() - secondTask.getDate()) > 1) {
            return false;
        }
    
        // Calculate the end times of both tasks
        double firstTaskEndTime = firstTask.getStartTime() + firstTask.getDuration();
        if (firstTaskEndTime > 23.75) {
            firstTaskEndTime -= 24.0;
            firstTaskEndDate += 1;
        }
        double secondTaskEndTime = secondTask.getStartTime() + secondTask.getDuration();
        if (secondTaskEndTime > 23.75) {
            secondTaskEndTime -= 24.0;
            secondTaskEndDate += 1;
        }
        
        // Check if date and start time of both tasks match
        if (firstTask.getDate() == secondTask.getDate() && firstTask.getStartTime() == secondTask.getStartTime()) {
            return true;
        }
            
        // Check if tasks overlap on the same date
        if (firstTask.getDate() == firstTaskEndDate && secondTask.getDate() == secondTaskEndDate && firstTask.getDate() == secondTask.getDate() && ((firstTask.getStartTime() < secondTask.getStartTime() && secondTask.getStartTime() < firstTaskEndTime)
            || (secondTask.getStartTime() < firstTask.getStartTime() && firstTask.getStartTime() < secondTaskEndTime))) {
            return true;
        }
            
        // Check if both tasks start on the same date and either one includes two dates where times overlap
        if (firstTask.getDate() == secondTask.getDate() && ((firstTaskEndDate > secondTaskEndDate && firstTaskEndTime < secondTaskEndTime) || (secondTaskEndDate > firstTaskEndDate && secondTaskEndTime < firstTaskEndTime))) {
            return true;
        }
            
        // Check if both tasks end on the same date and either one includes two dates where times overlap
        if (firstTaskEndDate == secondTaskEndDate && ((firstTask.getDate() < secondTask.getDate() && firstTaskEndTime > secondTask.getStartTime()) || (secondTask.getDate() < firstTask.getDate() && secondTaskEndTime > firstTask.getStartTime()))) {
            return true;
        }
            
        // Check if both tasks include 2 dates that both match
        if (firstTask.getDate() != firstTaskEndDate && secondTask.getDate() != secondTaskEndDate && firstTask.getDate() == secondTask.getDate() && firstTaskEndDate == secondTaskEndDate) {
            return true;
        }
        
        return false;
    }
}
