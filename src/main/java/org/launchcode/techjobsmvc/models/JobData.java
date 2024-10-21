package org.launchcode.techjobsmvc.models;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.launchcode.techjobsmvc.NameSorter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class JobData {

    private static final String DATA_FILE = "job_data.csv";
    private static boolean isDataLoaded = false;

    private static ArrayList<Job> allJobs = new ArrayList<>();
    private static ArrayList<Employer> allEmployers = new ArrayList<>();
    private static ArrayList<Location> allLocations = new ArrayList<>();
    private static ArrayList<PositionType> allPositionTypes = new ArrayList<>();
    private static ArrayList<CoreCompetency> allCoreCompetency = new ArrayList<>();

    // Fetch list of all job objects from loaded data, without duplicates, then return a copy
    public static ArrayList<Job> findAll() {
        loadData();
        return new ArrayList<>(allJobs);
    }

    // Returns the results of searching the Jobs data by field and search term
    public static ArrayList<Job> findByColumnAndValue(String column, String value) {
        loadData();

        ArrayList<Job> jobs = new ArrayList<>();
        if (value.toLowerCase().equals("all")) {
            return findAll();
        }

        if (column.equals("all")) {
            return findByValue(value);
        }

        for (Job job : allJobs) {
            String aValue = getFieldValue(job, column);
            if (aValue != null && aValue.toLowerCase().contains(value.toLowerCase())) {
                jobs.add(job);
            }
        }

        return jobs;
    }

    // Get the value of a specific field for a job (employer, location, etc.)
    public static String getFieldValue(Job job, String fieldName) {
        String theValue = null;

        if (fieldName.equals("name")) {
            theValue = job.getName();
        } else if (fieldName.equals("employer")) {
            theValue = job.getEmployer().getValue();
        } else if (fieldName.equals("location")) {
            theValue = job.getLocation().getValue();
        } else if (fieldName.equals("positionType")) {
            theValue = job.getPositionType().getValue();
        } else if (fieldName.equals("coreCompetency")) {
            theValue = job.getCoreCompetency().getValue();
        }

        return theValue;
    }

    // Search all job fields for the given term
    public static ArrayList<Job> findByValue(String value) {
        loadData();

        ArrayList<Job> jobs = new ArrayList<>();
        for (Job job : allJobs) {
            if (job.getName().toLowerCase().contains(value.toLowerCase())
                    || job.getEmployer().getValue().toLowerCase().contains(value.toLowerCase())
                    || job.getLocation().getValue().toLowerCase().contains(value.toLowerCase())
                    || job.getPositionType().getValue().toLowerCase().contains(value.toLowerCase())
                    || job.getCoreCompetency().getValue().toLowerCase().contains(value.toLowerCase())) {
                jobs.add(job);
            }
        }

        return jobs;
    }

    // Load data from CSV file and store it in an ArrayList of job objects
    private static void loadData() {
        if (isDataLoaded) {
            return;
        }

        try {
            Resource resource = new ClassPathResource(DATA_FILE);
            InputStream is = resource.getInputStream();
            Reader reader = new InputStreamReader(is);
            CSVParser parser = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(reader);
            List<CSVRecord> records = parser.getRecords();
            Integer numberOfColumns = records.get(0).size();

            for (CSVRecord record : records) {
                String aName = record.get(0);
                String anEmployer = record.get(1);
                String aLocation = record.get(2);
                String aPosition = record.get(3);
                String aSkill = record.get(4);

                Employer newEmployer = (Employer) findExistingObject(allEmployers, anEmployer);
                Location newLocation = (Location) findExistingObject(allLocations, aLocation);
                PositionType newPosition = (PositionType) findExistingObject(allPositionTypes, aPosition);
                CoreCompetency newSkill = (CoreCompetency) findExistingObject(allCoreCompetency, aSkill);

                if (newEmployer == null) {
                    newEmployer = new Employer(anEmployer);
                    allEmployers.add(newEmployer);
                }

                if (newLocation == null) {
                    newLocation = new Location(aLocation);
                    allLocations.add(newLocation);
                }

                if (newSkill == null) {
                    newSkill = new CoreCompetency(aSkill);
                    allCoreCompetency.add(newSkill);
                }

                if (newPosition == null) {
                    newPosition = new PositionType(aPosition);
                    allPositionTypes.add(newPosition);
                }

                Job newJob = new Job(aName, newEmployer, newLocation, newPosition, newSkill);
                allJobs.add(newJob);
            }

            isDataLoaded = true;
        } catch (IOException e) {
            System.out.println("Failed to load job data");
            e.printStackTrace();
        }
    }

    // Helper method to check for existing objects (employers, locations, etc.)
    private static Object findExistingObject(ArrayList list, String value) {
        for (Object item : list) {
            if (item.toString().toLowerCase().equals(value.toLowerCase())) {
                return item;
            }
        }
        return null;
    }

    // Methods to return lists of employers, locations, position types, and core competencies
    public static ArrayList<Employer> getAllEmployers() {
        loadData();
        allEmployers.sort(new NameSorter());
        return allEmployers;
    }

    public static ArrayList<Location> getAllLocations() {
        loadData();
        allLocations.sort(new NameSorter());
        return allLocations;
    }

    public static ArrayList<PositionType> getAllPositionTypes() {
        loadData();
        allPositionTypes.sort(new NameSorter());
        return allPositionTypes;
    }

    public static ArrayList<CoreCompetency> getAllCoreCompetency() {
        loadData();
        allCoreCompetency.sort(new NameSorter());
        return allCoreCompetency;
    }
}