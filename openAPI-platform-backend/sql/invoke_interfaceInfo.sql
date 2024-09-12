select interfaceInfoId, sum(totalNum) as num
from user_interface_info
group by interfaceInfoId
order by num desc
limit 5