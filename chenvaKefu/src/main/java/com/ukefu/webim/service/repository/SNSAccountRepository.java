package com.ukefu.webim.service.repository;

import com.ukefu.webim.web.model.SNSAccount;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public abstract interface SNSAccountRepository
  extends JpaRepository<SNSAccount, String>
{
  public abstract SNSAccount findById(String paramString);
  
  public abstract SNSAccount findBySnsid(String snsid);
  
  public abstract int countByAppkey(String appkey);
  
  public abstract List<SNSAccount> findBySnstype(String paramString);
}
