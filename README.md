## dapeng-soa : api-gateway 基础服务
Example：
````
method: post
url: http://127.0.0.1:8888/api/{serviceName}/{version}/{methodName}
parameter: parameter = {"body":{"request":{"bizTag":"suplier","step":1}}}

method: post
url: http://127.0.0.1:8888/api
parameter: 
serviceName={serviceName}
version={version}
methodName={methodName}
parameter = {"body":{"request":{"bizTag":"suplier","step":1}}}
````