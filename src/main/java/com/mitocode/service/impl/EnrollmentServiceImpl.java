package com.mitocode.service.impl;


import com.mitocode.model.Course;
import com.mitocode.model.Enrollment;
import com.mitocode.repo.ICourseRepo;
import com.mitocode.repo.IEnrollmentRepo;
import com.mitocode.repo.IGenericRepo;
import com.mitocode.repo.IStudentRepo;
import com.mitocode.service.IEnrollmentService;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl extends CRUDImpl<Enrollment, String> implements IEnrollmentService {
    private final IEnrollmentRepo enrollmentRepo;
    private final ICourseRepo courseRepo;
    private final IStudentRepo studentRepo;
    @Override
    protected IGenericRepo<Enrollment, String> getRepo() {
        return enrollmentRepo;
    }

    @Override
    public Mono<byte[]> generateReport(String idEnrollment) {
        return enrollmentRepo.findById(idEnrollment)
                // Obteniendo Student
                .flatMap(enr-> Mono.just(enr)
                        .zipWith(studentRepo.findById(enr.getStudent().getId()), (en, st) -> {
                            en.setStudent(st);
                            return en;
                        })
                )
                // Obteniendo Course
                .flatMap(enr ->{
                    return Flux.fromIterable(enr.getCourses())
                            .flatMap(cour ->{
                                return courseRepo.findById(cour.getId())
                                        .map(c -> {
                                            cour.setId(c.getId());
                                            cour.setName(c.getName());
                                            cour.setAcronym(c.getAcronym());
                                            cour.setStatus(c.getStatus());
                                            return cour;
                                        });
                            })
                            .collectList().flatMap(list ->{
                                enr.setCourses(list);
                                return Mono.just(enr);
                            });

                })
                .map(enr -> {
                    try{
                        Map<String, Object> parameters = new HashMap<>();
                        parameters.put("student_name", enr.getStudent().getName());
                        parameters.put("student_lastname", enr.getStudent().getLastname());
                        parameters.put("student_dni", enr.getStudent().getDni());
                        parameters.put("student_age", enr.getStudent().getAge());

                        InputStream stream = getClass().getResourceAsStream("/enrollments.jrxml");
                        JasperReport report = JasperCompileManager.compileReport(stream);
                        JasperPrint print = JasperFillManager.fillReport(report, parameters, new JRBeanCollectionDataSource(enr.getCourses()));
                        return JasperExportManager.exportReportToPdf(print);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    return new byte[0];
                });
    }
}
