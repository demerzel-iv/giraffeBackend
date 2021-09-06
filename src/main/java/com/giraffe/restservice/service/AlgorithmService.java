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

        limit *= 2;

        for (MyEntity entity : source) {
            int eid = entity.getId();
            if (!vis.contains(eid)) {
                queue.offer(eid);
                vis.add(eid);
            }
        }
        while (!queue.isEmpty()) {
            int eid = queue.poll();
            for (int i = 0; i < 3; i++) {
                try {
                    retList.addAll(problemService.getProblemList(entityDAO.findById(eid).get().getLabel()));
                    break;
                } catch (NetworkRequestFailedException e) {
                }
            }

            if (retList.size() >= limit) {
                break;
            }
            for (int neid : edgeDAO.findNeighborhood(eid)) {
                if (!vis.contains(neid)) {
                    queue.offer(neid);
                    vis.add(neid);
                }
            }
        }

        limit /= 2;
        Collections.shuffle(retList);
        int realLimit = limit;
        if (retList.size() < realLimit) {
            realLimit = retList.size();
        }
        return retList.subList(0, realLimit);
    }
}
