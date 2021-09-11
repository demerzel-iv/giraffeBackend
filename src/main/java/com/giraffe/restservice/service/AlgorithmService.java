package com.giraffe.restservice.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.giraffe.restservice.dao.EdgeDAO;
import com.giraffe.restservice.dao.MyEntityDAO;
import com.giraffe.restservice.exception.NetworkRequestFailedException;
import com.giraffe.restservice.pojo.MyEntity;

import org.springframework.stereotype.Service;

@Service
public class AlgorithmService {
    private EdgeDAO edgeDAO;
    private MyEntityDAO entityDAO;
    private ProblemService problemService;

    AlgorithmService(EdgeDAO edgeDAO, MyEntityDAO entityDAO, ProblemService problemService) {
        this.edgeDAO = edgeDAO;
        this.entityDAO = entityDAO;
        this.problemService = problemService;
    }

    public List<MyEntity> getRelativeEntities(List<MyEntity> source, int limit) {
        HashSet<Integer> vis = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        ArrayList<MyEntity> retList = new ArrayList<>();

        for (MyEntity entity : source) {
            int eid = entity.getId();
            if (!vis.contains(eid)) {
                queue.offer(eid);
                vis.add(eid);
            }
        }
        while (!queue.isEmpty()) {
            int eid = queue.poll();
            retList.add(entityDAO.findById(eid).get());
            if (retList.size() == limit) {
                break;
            }
            for (int neid : edgeDAO.findNeighborhood(eid)) {
                if (!vis.contains(neid)) {
                    queue.offer(neid);
                    vis.add(neid);
                }
            }
        }
        return retList;
    }

    public List<Object> getRelativeProblems(List<MyEntity> source, int limit) {
        HashSet<Integer> vis = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        ArrayList<Object> retList = new ArrayList<>();

        for (MyEntity entity : source) {
            int eid = entity.getId();
            if (!vis.contains(eid)) {
                queue.offer(eid);
                vis.add(eid);
            }
        }

        ArrayList<MyEntity> entityList = new ArrayList<>();

        while (!queue.isEmpty()) {
            int eid = queue.poll();

            entityList.add(entityDAO.findById(eid).get());

            if (entityList.size() >= limit * 5) {
                break;
            }
            for (int neid : edgeDAO.findNeighborhood(eid)) {
                if (!vis.contains(neid)) {
                    queue.offer(neid);
                    vis.add(neid);
                }
            }
        }

        for (MyEntity entity : entityList) {
            for (int i = 0; i < 3; i++) {
                try {
                    retList.addAll(problemService.getProblemList(entity.getLabel()));
                    break;
                } catch (NetworkRequestFailedException e) {
                }
            }
            if (retList.size() > limit * 10) {
                break;
            }
        }

        Collections.shuffle(retList);

        if (retList.size() < limit) {
            List<String> courseList = new ArrayList<>();
            courseList.add("biology");
            courseList.add("chinese");
            courseList.add("geo");
            courseList.add("math");
            courseList.add("politics");
            courseList.add("chemistry");
            courseList.add("english");
            courseList.add("history");
            courseList.add("physics");

            List<MyEntity> tmpList = new ArrayList<>();
            for (String course : courseList) {
                List<MyEntity> eList = entityDAO.getEntityByCourse(course);
                eList = eList.subList(0, 10);
                tmpList.addAll(eList);

            }
            Collections.shuffle(tmpList);

            retList.addAll(getRelativeProblems(tmpList, limit));
        }

        return retList.subList(0, limit);
    }
}
