### Act Cache
- **Distribute Consistance Cache**
- High perfomance: local cache and redis cache
- Easy usage: @Cacheable,@Cachevict

### Problem
- local caches are not consistence because of diffrent load time
![](https://ws2.sinaimg.cn/large/006tNc79ly1ftl3f4oaahj30s20jowgc.jpg)

### Design
- the main idea inherits from Zab(zooKeepr atomic broadcast)
	- 1.The leader sends a PROPOSAL message, p, to all followers.

	- 2.Upon receiving p, a follower responds to the leader with an ACK, informing the leader that it has accepted the proposal.

	- 3.Upon receiving acknowledgments from a quorum (the quorum includes the leader itself), the leader sends a message informing the followers to COMMIT it.
	- 4.the procedure as follows:
	 ![](https://ws2.sinaimg.cn/large/006tNc79ly1ftl28y94z7j30z40gmju5.jpg)

- So I design this architecture.
![](https://ws3.sinaimg.cn/large/006tNc79ly1ftl1qn4etyj30vj0mm40h.jpg)

- L1:leader
- F1-F3:client
- ZK:zookeepr
