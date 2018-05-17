import os
import urllib
import urllib3
import http
from mxnet import image

uriBase = "https://westcentralus.api.cognitive.microsoft.com/vision/v2.0/analyze"
urlBase = "https://westcentralus.api.cognitive.microsoft.com/vision/v2.0"
requestParameters = "visualFeatures=Categories,Description,Color"
subscriptionKey = "65daceefe43f421a98e9319a4fc55cac"

def get_img_to_bytes(file_name):
    img = image.imdecode(open(file_name, 'rb').read())
    # print('[img]', img)
    x = img.asnumpy().tobytes()
    print('[x]', x)
    return x

if __name__ == '__main__':
    # url = urlBase + "?" + requestParameters
    #
    # http = urllib3.PoolManager()
    # # 通过request()方法创建一个请求：
    # # r = http.request('GET', url='https://www.baidu.com', headers={'Content-Type': 'application/json'})
    # r = http.request('GET', url=url, headers={
    #     'Ocp-Apim-Subscription-Key': subscriptionKey,
    #     'Content-Type': 'application/json'})
    # print('[status]', r.status)  # 200
    # # 获得html源码,utf-8解码
    # print(r.data.decode())
    #
    # import http.client, urllib.request, urllib.parse, urllib.error, base64

    img_data = get_img_to_bytes('D:/DeepLearning/object_detection/timg.jpg')

    headers = {
        # Request headers
        'Content-Type': 'application/json',
        'Ocp-Apim-Subscription-Key': '65daceefe43f421a98e9319a4fc55cac',
    }

    params = urllib.parse.urlencode({
        # Request parameters
        'visualFeatures': 'Categories,Description,Color',
        'language': 'en',
    })

    img_str = 'https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1548707859,2504596459&amp;fm=27&amp;gp=0.jpg'
    img_url = {'url':'https://image.baidu.com/search/detail?ct=503316480&z=0&ipn=d&word=%E8%A1%97%E6%99%AF&hs=2&pn=3&spn=0&di=109209258730&pi=0&rn=1&tn=baiduimagedetail&is=0%2C0&ie=utf-8&oe=utf-8&cl=2&lm=-1&cs=3639028483%2C3086135838&os=2576906568%2C2218329849&simid=4110421773%2C710543994&adpicid=0&lpn=0&ln=30&fr=ala&fm=&sme=&cg=&bdtype=0&oriquery=%E8%A1%97%E6%99%AF&objurl=http%3A%2F%2Fs14.sinaimg.cn%2Fmw690%2F001GSrSEzy70g31Owe90d%26690&fromurl=ippr_z2C%24qAzdH3FAzdH3Fks52_z%26e3Bftgw_z%26e3Bv54_z%26e3BvgAzdH3FfAzdH3Fks52_nkw99ld9a8adohth_z%26e3Bip4s&gsm=0&islist=&querylist='}
    try:
        conn = http.client.HTTPSConnection('westcentralus.api.cognitive.microsoft.com')
        # conn = http.client.HTTPSConnection('westus.api.cognitive.microsoft.com')
        # conn.request("POST", "/vision/v2.0/analyze?%s" % params, img_data, headers)
        conn.request("POST", "/vision/v2.0/analyze?%s" % params, img_str, headers)
        response = conn.getresponse()
        data = response.read()
        print('[date]', data)
        conn.close()
    except Exception as e:
        print(e.errno, e.strerror)