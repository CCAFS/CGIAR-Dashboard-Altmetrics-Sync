/*****************************************************************
 * This file is part of Managing Agricultural Research for Learning &
 * Outcomes Platform (MARLO).
 * MARLO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * MARLO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with MARLO. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.manager.implementation;

import org.cgiar.ccafs.dao.AltmetricsErrorLogDAO;
import org.cgiar.ccafs.domain.marlo.AltmetricsErrorLog;
import org.cgiar.ccafs.manager.AltmetricsErrorLogManager;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

@Service
@Scope("singleton")
public class AltmetricsErrorLogManagerImpl implements AltmetricsErrorLogManager {

  @Autowired
  AltmetricsErrorLogDAO altmetricsErrorLogDAO;

  @Autowired
  Validator validator;

  @Override
  @Transactional(readOnly = true)
  public Long count() {
    return altmetricsErrorLogDAO.count();
  }

  @Override
  @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public void delete(AltmetricsErrorLog entity) throws Exception {
    Long id = entity.getId();
    if (id == null || id < 1) {
      throw new Exception("Invalid id");
    }

    if (altmetricsErrorLogDAO.existsById(id) == false) {
      throw new Exception("Entity does not exist in the database");
    }

    entity = altmetricsErrorLogDAO.findById(id).get();

    // altmetricsErrorLogDAO.delete(entity);
  }

  @Override
  @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public void deleteById(Long id) throws Exception {
    if (id == null || id < 1) {
      throw new Exception("Invalid id");
    }

    if (altmetricsErrorLogDAO.existsById(id) == false) {
      throw new Exception("Entity does not exist in the database");
    }

    // delete(findById(id).get());
  }

  @Override
  @Transactional(readOnly = true)
  public List<AltmetricsErrorLog> findAll() {
    return altmetricsErrorLogDAO.findAll();
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<AltmetricsErrorLog> findById(Long id) {
    return altmetricsErrorLogDAO.findById(id);
  }

  @Override
  @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public AltmetricsErrorLog save(AltmetricsErrorLog entity) throws Exception {
    this.validate(entity);
    altmetricsErrorLogDAO.save(entity);
    return entity;
  }

  @Override
  @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public AltmetricsErrorLog update(AltmetricsErrorLog entity) throws Exception {
    this.validate(entity);

    Long id = entity.getId();
    if (id == null || id < 1) {
      throw new Exception("Invalid id");
    }

    if (altmetricsErrorLogDAO.existsById(id) == false) {
      throw new Exception("Entity does not exist in the database");
    }

    altmetricsErrorLogDAO.save(entity);
    return entity;
  }

  @Override
  public void validate(AltmetricsErrorLog entity) throws Exception {
    Objects.requireNonNull(entity);

    Set<ConstraintViolation<AltmetricsErrorLog>> constraintViolations = validator.validate(entity);

    if (!constraintViolations.isEmpty()) {
      StringBuilder strMessage = new StringBuilder();

      for (ConstraintViolation<AltmetricsErrorLog> constraintViolation : constraintViolations) {
        strMessage.append(constraintViolation.getPropertyPath().toString());
        strMessage.append(" - ");
        strMessage.append(constraintViolation.getMessage());
        strMessage.append(". \n");
      }

      throw new Exception(strMessage.toString());
    }
  }

}
