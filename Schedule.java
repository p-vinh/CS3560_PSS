import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Schedule {
    
    public List<Task> dailySchedule(List<Task> tasks, int targetDate) {
        List<Task> schedule = new ArrayList<Task>();

        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.getDate() == targetDate) {
                if (task.getClass() == TransientTask.class) {
                    schedule.add(task);
                } else if (task.getClass() == RecursiveTask.class) {
                    int date = task.getDate();
                    double startTime = task.getTime();
                    double duration = task.getDuration();
                    boolean antiTaskFound = false;
                    for (int j = i + 1; j < tasks.size(); j++) {
                        Task potentialAntiTask = tasks.get(j);
                        if (potentialAntiTask.getClass() == AntiTask.class
                                && potentialAntiTask.getDate() == date
                                && potentialAntiTask.getTime() == startTime
                                && potentialAntiTask.getDuration() == duration) {
                            antiTaskFound = true;
                            break;
                        }
                    }
                    if (!antiTaskFound) {
                        schedule.add(task);
                    }
                }
            }
        }

        return schedule.stream()
                       .sorted(Comparator.comparing(Task::getTime))
                       .collect(Collectors.toList());
    }

    public List<Task> weeklySchedule(List<Task> tasks, int targetDate) {
        List<Task> schedule = new ArrayList<Task>();

        LocalDate startDate = LocalDate.parse(String.valueOf(targetDate), DateTimeFormatter.BASIC_ISO_DATE);
        LocalDate endOfWeekDate = startDate.plusDays(7);
        int targetEndDate = Integer.parseInt(endOfWeekDate.format(DateTimeFormatter.BASIC_ISO_DATE));

        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.getDate() >= targetDate && task.getDate() <= targetEndDate) {
                if (task.getClass() == TransientTask.class) {
                    schedule.add(task);
                } else if (task.getClass() == RecursiveTask.class) {
                    int date = task.getDate();
                    double startTime = task.getTime();
                    double duration = task.getDuration();
                    boolean antiTaskFound = false;
                    for (int j = i + 1; j < tasks.size(); j++) {
                        Task potentialAntiTask = tasks.get(j);
                        if (potentialAntiTask.getClass() == AntiTask.class
                                && potentialAntiTask.getDate() == date
                                && potentialAntiTask.getTime() == startTime
                                && potentialAntiTask.getDuration() == duration) {
                            antiTaskFound = true;
                            break;
                        }
                    }
                    if (!antiTaskFound) {
                        schedule.add(task);
                    }
                }
            }
        }

        return schedule.stream()
                       .sorted(Comparator.comparing(Task::getTime))
                       .sorted(Comparator.comparing(Task::getDate))
                       .collect(Collectors.toList());
    }

    public List<Task> monthlySchedule(List<Task> tasks, int targetDate) {
        List<Task> schedule = new ArrayList<Task>();

        int targetEndDate = endDateOfMonth(targetDate);

        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.getDate() >= targetDate && task.getDate() <= targetEndDate) {
                if (task.getClass() == TransientTask.class) {
                    schedule.add(task);
                } else if (task.getClass() == RecursiveTask.class) {
                    int date = task.getDate();
                    double startTime = task.getTime();
                    double duration = task.getDuration();
                    boolean antiTaskFound = false;
                    for (int j = i + 1; j < tasks.size(); j++) {
                        Task potentialAntiTask = tasks.get(j);
                        if (potentialAntiTask.getClass() == AntiTask.class
                                && potentialAntiTask.getDate() == date
                                && potentialAntiTask.getTime() == startTime
                                && potentialAntiTask.getDuration() == duration) {
                            antiTaskFound = true;
                            break;
                        }
                    }
                    if (!antiTaskFound) {
                        schedule.add(task);
                    }
                }
            }
        }

        return schedule.stream()
                       .sorted(Comparator.comparing(Task::getTime))
                       .sorted(Comparator.comparing(Task::getDate))
                       .collect(Collectors.toList());
    }

    private int endDateOfMonth(int targetDate) {
        LocalDate startDate = LocalDate.parse(String.valueOf(targetDate), DateTimeFormatter.BASIC_ISO_DATE);
        LocalDate lastDayOfMonth;
        if (startDate.getDayOfMonth() == 1) {
            lastDayOfMonth = startDate.withDayOfMonth(startDate.lengthOfMonth());
        } else if (startDate.getDayOfMonth() == startDate.lengthOfMonth() && startDate.lengthOfMonth() > startDate.plusMonths(1).lengthOfMonth()) {
            lastDayOfMonth = startDate.plusMonths(1);
        } else {
            lastDayOfMonth = startDate.plusMonths(1).minusDays(1);
        }
        return Integer.parseInt(lastDayOfMonth.format(DateTimeFormatter.BASIC_ISO_DATE));
    }

    public List<Task> fullSchedule(List<Task> tasks) {
        List<Task> schedule = new ArrayList<Task>();

        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.getClass() == TransientTask.class) {
                schedule.add(task);
            } else if (task.getClass() == RecursiveTask.class) {
                int date = task.getDate();
                double startTime = task.getTime();
                double duration = task.getDuration();
                boolean antiTaskFound = false;
                for (int j = i + 1; j < tasks.size(); j++) {
                    Task potentialAntiTask = tasks.get(j);
                    if (potentialAntiTask.getClass() == AntiTask.class
                            && potentialAntiTask.getDate() == date
                            && potentialAntiTask.getTime() == startTime
                            && potentialAntiTask.getDuration() == duration) {
                        antiTaskFound = true;
                        break;
                    }
                }
                if (!antiTaskFound) {
                    schedule.add(task);
                }
            }
        }

        return schedule.stream()
                       .sorted(Comparator.comparing(Task::getTime))
                       .sorted(Comparator.comparing(Task::getDate))
                       .collect(Collectors.toList());
    }
}
