package com.cafa.pdf.core.service;

import com.cafa.pdf.core.commom.dto.AuthorDTO;
import com.cafa.pdf.core.commom.enums.ActiveEnum;
import com.cafa.pdf.core.commom.enums.CheckEnum;
import com.cafa.pdf.core.commom.shiro.CryptographyUtil;
import com.cafa.pdf.core.dal.dao.AuthorDAO;
import com.cafa.pdf.core.dal.dao.CheckDAO;
import com.cafa.pdf.core.dal.dao.IBaseDAO;
import com.cafa.pdf.core.dal.entity.Author;
import com.cafa.pdf.core.dal.entity.Check;
import com.cafa.pdf.core.util.MStringUtils;
import com.cafa.pdf.core.util.ObjectConvertUtil;
import com.cafa.pdf.core.util.RequestHolder;
import com.cafa.pdf.core.web.request.BasicSearchReq;
import com.cafa.pdf.core.web.request.author.AuthorRegisterReq;
import com.cafa.pdf.core.web.request.author.AuthorSaveReq;
import com.cafa.pdf.core.web.request.author.AuthorSearchReq;
import com.cafa.pdf.core.web.request.author.AuthorUpdateReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional(readOnly = true)
public class AuthorService extends ABaseService<Author, Long> {

    private final AuthorDAO authorDAO;
    private final CheckDAO checkDAO;
    private final MailComponent mailComponent;

    @Autowired
    public AuthorService(AuthorDAO authorDAO, CheckDAO checkDAO, MailComponent mailComponent) {
        this.authorDAO = authorDAO;
        this.checkDAO = checkDAO;
        this.mailComponent = mailComponent;
    }

    @Override
    protected IBaseDAO<Author, Long> getEntityDAO() {
        return authorDAO;
    }

    public Author findByTelephone(String telephone) {
        return authorDAO.findByTelephone(telephone);
    }

    public Author findByUsername(String username) {
        return authorDAO.findByUsername(username);
    }

    public Long getCount() {
        return authorDAO.count();
    }

    @Transactional(readOnly = false)
    public void delete(Long id) {
        // 并不是真正的删除，只是冻结账户
        Author author = findById(id);
        author.setActive(0);
        update(author);
    }

    @Transactional
    public void save(AuthorSaveReq authorSaveReq) {
        log.info("【新增著作者】 {}", authorSaveReq);
        Author author = new Author();
        ObjectConvertUtil.objectCopy(author, authorSaveReq);
        author.setCreatedTime(new Date());
        author.setModifiedTime(new Date());
        this.update(author);
    }

    @Transactional
    public void update(AuthorUpdateReq authorUpdateReq) {
        log.info("【更新著作者】 {}", authorUpdateReq);
        Author author = findById(authorUpdateReq.getId());
        ObjectConvertUtil.objectCopy(author, authorUpdateReq);
        author.setModifiedTime(new Date());
        this.update(author);
    }

    @Transactional
    public void register(AuthorRegisterReq authorRegisterReq) {
        log.info("【注册著作者】 {}", authorRegisterReq);
        Author author = new Author();
        ObjectConvertUtil.objectCopy(author, authorRegisterReq);
        author.setCreatedTime(new Date());
        author.setModifiedTime(new Date());
        author.setPassword(CryptographyUtil.cherishSha1(author.getPassword()));
        // 待邮箱激活
        author.setActive(ActiveEnum.UN_CHECK_EMAIL.getNum());

        author = this.save(author);
        // 发送邮件
        this.sendAuthorEmail(author);
    }

    public Page<AuthorDTO> findAll(BasicSearchReq basicSearchReq, AuthorSearchReq authorSearchReq) {
        int pageNumber = basicSearchReq.getStartIndex() / basicSearchReq.getPageSize() + 1;
        Page<Author> authorPage = super.findAllBySearchParams(
                buildSearchParams(authorSearchReq), pageNumber, basicSearchReq.getPageSize());
        return authorPage.map(this::getAuthorDTO);
    }

    /**
     * 转成DTO
     * @param source Author
     * @return AuthorDTO
     */
    private AuthorDTO getAuthorDTO(Author source) {
        AuthorDTO authorDTO = new AuthorDTO();
        ObjectConvertUtil.objectCopy(authorDTO, source);
        authorDTO.setActiveStr(ActiveEnum.getDesc(source.getActive()));
        return authorDTO;
    }

    /**
     * 检测该邮箱是否已存在，
     * @param email 邮箱
     * @return 存在:true 不存在或邮箱未验证：false
     */
    @Transactional
    public boolean existEmail(String email) {
        Author author = authorDAO.findByEmail(email);
        if (author == null) {
            return false;
        }
        if (author.getActive().equals(ActiveEnum.UN_CHECK_EMAIL.getNum())){
            //邮箱未验证的，删除他，返回false
            authorDAO.delete(author);
            return false;
        }
        return true;
    }
    @Transactional
    public boolean existUsername(String username) {
        Author author = authorDAO.findByUsername(username);
        if (author == null) {
            return false;
        }
        if (author.getActive().equals(ActiveEnum.UN_CHECK_EMAIL.getNum())){
            //邮箱未验证的，删除他，返回false
            authorDAO.delete(author);
            return false;
        }
        return true;
    }

    /**
     * 邮箱验证激活著作者
     * @param checkId
     * @param key
     * @return
     */
    @Transactional
    public boolean active(Long checkId, String key) {
        log.info("【激活著作者】 checkId:{} key:{}", checkId, key);
        Check check = checkDAO.findOne(checkId);
        log.info("【激活著作者】 check:{}", check);
        if (check == null || !check.getRandomKey().equals(key)) {
            return false;
        }
        // 激活著作者账号
        Author author = authorDAO.findOne(check.getActiveId());
        author.setActive(ActiveEnum.ACTIVE.getNum());
        authorDAO.save(author);
        // 删除邮箱核实信息
        checkDAO.delete(check.getId());
        return true;
    }

    /**
     * 发邮件给著作者，验证所填写邮箱
     * @param author 著作者信息
     * @return 是否成功发送
     */
    @Transactional
    private boolean sendAuthorEmail(Author author){
        String randomStr = MStringUtils.randomStr();
        Check check = new Check(0L, randomStr, CheckEnum.AUTHOR.getNum(), author.getId());
        check = checkDAO.save(check);

        String basePath = MStringUtils.getBasePath(RequestHolder.getRequest());
        // http://www.domain.com/author/666/active?key=uuid
        String link = basePath + "author/" + check.getId() + "/" + "active?key=" + randomStr;
        log.info("【发送邮件】 link:{} check:{} author:{}", link, check, author);

        String subject = "邮箱激活 - 广东科技著作出版服务平台";
        StringBuilder sb = new StringBuilder();
        sb.append("欢迎您加入成为著作者！<br><br>");
        sb.append("您的账号已经成功创建，请点击以下链接激活账号：<br><br>");
        sb.append("<a href='" + link + "'>" + link + "</a><br><br>");
        sb.append("请妥善保管此电子邮件。<br><br>");

        sb.append("<b>注意：</b><br><br>");
        sb.append("1、管理员要核实您的信息才为您开放其他权限<br><br>");

        sb.append("<br><br><b>广东科技著作出版服务平台</b><br><br>");

        mailComponent.sendHtmlMail(author.getEmail(), subject, sb.toString());
        return true;
    }



}
