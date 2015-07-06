package com.calendar;


/** @class Date
 * 
 * @author Nicolas
 *
 *	A Date is represented by the year, the month from 0 to 11, the day, the hour and the minute.
 *	Bissextile years are recognized by the following rules : either (a multiple of 4 but not of 100) or (a multiple of 400) 
 *
 */

//This class should be used to implemente the rehearsal programs
public class Date {
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;
	private boolean leapYear; // whether the year is Bissextile or not, only used in addMonth and addDay
	private int[] months;	  // the right array of month depending on the year (Bissextile or not), also only used in addMonth and addDay
	private int[] monthOfLeapYear = { 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
	private int[] monthOfNotLeapYear = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

	/** Create a new date */
	public Date() {
		
	}
	
	/** Create a new date with the given parameters.<br>
	 * Always create a valid date. If you set month to 14, it will add a year
	 * to the number of year you've chosen
	 * 
	 * @param year The year of the date
	 * @param month The month of the date (from 0 for January to 11 for December)
	 * @param day The day of the date (from 1 to the number of days of this month)
	 * @param hour The hour of the date
	 * @param minute The minute of the date
	 * 
	 */
	public Date(int year, int month, int day, int hour, int minute) {
		this.addMinute(minute);
		this.addHour(hour);
		this.addDay(day);
		this.addMonth(month);
		this.addYear(year);
	}

	/** Copy a date */
	public Date copy() {
		Date dateCopy = new Date();
		dateCopy.minute = this.minute;
		dateCopy.hour = this.hour;
		dateCopy.day = this.day;
		dateCopy.month = this.month;
		dateCopy.year = this.year;
		return dateCopy;
	}
	
	/** Check whether or not it's leap year.
	 * 
	 * This is a leap year if this a multiple of 4 but not of 100 or if it's a multiple of 400)
	 * 
	 */
	private boolean isLeapYear() {
		return ((this.year % 4 == 0 && this.year % 100 != 0) || this.year % 400 == 0);
	}

	/** Add the number of years given to the current date
	 * <br>
	 * In case the Date was the 29th of February, and the result isn't a leap year, return the 1st of March.
	 * 
	 * @param year The number of years to add
	 * 
	 */
	public Date addYear(int year) {
		this.year += year;
		if (day == 29 && month == 1 && !isLeapYear()) {
			day = 1;
			month = 2;
		}
		return this;
	}

	/** Add the number of months given to the current date
	 * <br>
	 * Adding a month to the 31st of January will return the 1st of March because
	 * February doesn't have 31 days.
	 * 
	 * @param month The number of months to add
	 * 
	 */
	public Date addMonth(int month) {
		int yearsToAdd = (this.month + month) / 12;
		this.month = (this.month + month) % 12;
		this.addYear(yearsToAdd);
		leapYear = this.isLeapYear();
		if (leapYear) {
			months = monthOfLeapYear;
		} else {
			months = monthOfNotLeapYear;
		}
		/* If the number of day is 30 and the current day is 31
		 * or if this is February,
		 * 		if this is a leap year, then check if current is less or equal to 29,
		 * 		and if it isn't a leap year, then check if current day is less or equal to 28,
		 * go to the next month and check if this is a new year
		 */
		if (((this.month == 3 || this.month == 5 || this.month == 8 || this.month == 10) && this.day == 31)
                  || (leapYear && this.month == 1 && this.day > 29) || (!leapYear && this.month == 1 && this.day > 28)) {
			this.month++;
			this.day = 1;
			/* If this month is January, then we've entered in a new year */
			if (this.month == 0) {
				this.year++;
			}
		}
		return this;
	}

	/** Adds the number of days given to the current date
	 * 
	 * @param day The number of days to add
	 * 
	 */
	public Date addDay(int day) {
		int daysToAdd = day;
		leapYear = this.isLeapYear();
		if (leapYear) {
			months = monthOfLeapYear;
		} else {
			months = monthOfNotLeapYear;
		}
		/* while the number of days added to the current day exceeds the number of days in the current month,
		 * go to the next month and remove the number of days of the former current month */
		while (this.day + daysToAdd > months[this.month]) {
			this.month = (this.month + 1) % 12;
			if (this.month == 0) {
				this.year++;
				leapYear = this.isLeapYear();
				if (leapYear) {
					months = monthOfLeapYear;
				} else {
					months = monthOfNotLeapYear;
				}
				daysToAdd -= months[11];
			} else {
				daysToAdd -= months[this.month - 1];
			}
		}
		this.day += daysToAdd;
		return this;
	}

	/** Adds the number of hours given to the current date
	 * 
	 * @param hour The number of hours to add
	 * 
	 */
	public Date addHour(int hour) {
		int daysToAdd = (this.hour + hour) / 24;
		this.hour = (this.hour + hour) % 24;
		this.addDay(daysToAdd);
		return this;
	}

	/** Adds the number of minutes given to the current date
	 * 
	 *  @param minute The number of minutes to add
	 *  
	 */
	public Date addMinute(int minute) {
		int hoursToAdd = (this.minute + minute) / 60;
		this.minute = (this.minute + minute) % 60;
		this.addHour(hoursToAdd);
		return this;
	}

	
	/** Adds the amout of time given to the current date starting with the smallest
	 * period of time and so on (minute then hour etc..)
	 * <br>
	 * So for example, adding a month and a day to the 31st of January will return
	 * the 1st of March and not the 2nd of March.
	 * 
	 *  @param year The number of years to add
	 *  @param month The number of months to add
	 *  @param day The number of days to add
	 *  @param hour The number of hours to add
	 *  @param minute The number of minutes to add
	 *  
	 */
	public Date addTime(int year, int month, int day, int hour, int minute) {
		this.addMinute(minute);
		this.addHour(hour);
		this.addDay(day);
		this.addMonth(month);
		this.addYear(year);
		return this;
	}

}
